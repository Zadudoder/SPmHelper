package zadudoder.spmhelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.glfw.GLFW;
import zadudoder.spmhelper.Screen.Calls.CallsScreen;
import zadudoder.spmhelper.Screen.Laws.LawsScreen;
import zadudoder.spmhelper.Screen.MainScreen;
import zadudoder.spmhelper.Screen.Map.MapScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.events.ModEvents;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.types.Card;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class SPmHelperClient implements ClientModInitializer {
    public static SPmHelperConfig config = null;

    private static KeyBinding keyOpenScreen;
    private static KeyBinding keyOpenCallsScreen;
    private static KeyBinding keyOpenPayScreen;
    private static KeyBinding keyOpenMapScreen;
    private static KeyBinding keyOpenLawsScreen;

    private static final String API_BASE = "https://api-spmhelpers.sp-mini.ru/api";
    private static String authToken = null;
    private static ScheduledExecutorService executorService;

    @Override
    public void onInitializeClient() {

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(SPmHelperConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(SPmHelperConfig.class).getConfig();
        }

        registerKeyBindings();
        registerKeyHandlers();

        ModEvents.registerEvents();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Команда для привязки карты
            dispatcher.register(
                    literal("spmhelper")
                            .then(argument("id", StringArgumentType.string())
                                    .then(argument("token", StringArgumentType.string())
                                            .executes(context -> {
                                                String id = StringArgumentType.getString(context, "id");
                                                String token = StringArgumentType.getString(context, "token");

                                                //SPmHelperConfig.setToken(id, token);
                                                SPmHelperClient.config.setSpID(id);
                                                SPmHelperClient.config.setSpTOKEN(token);
                                                AutoConfig.getConfigHolder(SPmHelperConfig.class).save();
                                                Card card = new Card(id, token);

                                                int balance = SPWorldsApi.getBalance(card);
                                                if (balance >= 0) {
                                                    context.getSource().sendFeedback(Text.literal(
                                                            "✅ Карта привязана! Баланс: " + balance + " AR"));
                                                } else {
                                                    context.getSource().sendError(Text.literal(
                                                            "❌ Ошибка: неверный токен или ID"));
                                                }
                                                return 1;
                                            })
                                    )
                            )
                            .then(literal("auth")
                                    .executes(context -> {
                                        startAuthProcess(context.getSource());
                                        return 1;
                                    }))
                            .then(literal("status")
                                    .executes(context -> {
                                        checkAuthStatus(context.getSource());
                                        return 1;
                                    }))
                    );
        });
    }

    private static void startAuthProcess(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Получаем UUID и удаляем дефисы
        String playerUuid = client.player.getUuid().toString().replace("-", "");
        System.out.println("Starting auth process for UUID: " + playerUuid);

        executorService = Executors.newSingleThreadScheduledExecutor();
        source.sendFeedback(Text.literal("§aНачинаем процесс авторизации через Discord..."));

        new Thread(() -> {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = API_BASE + "/authorize";
                System.out.println("POST to: " + url);

                HttpPost request = new HttpPost(url);
                request.setHeader("Content-Type", "application/json");

                JsonObject json = new JsonObject();
                json.addProperty("minecraft_uuid", playerUuid); // Теперь без дефисов
                request.setEntity(new StringEntity(json.toString()));

                String response = httpClient.execute(request, httpResponse -> {
                    int status = httpResponse.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    System.out.println("Response: " + status + " - " + body);
                    return body;
                });

                JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();

                if (responseJson.has("redirect_url")) {
                    String authUrl = responseJson.get("redirect_url").getAsString();
                    client.execute(() -> {
                        source.sendFeedback(Text.literal("§aПерейдите по ссылке для авторизации:"));
                        source.sendFeedback(Text.literal("§9" + authUrl));
                        Util.getOperatingSystem().open(authUrl);

                        executorService.scheduleAtFixedRate(() -> {
                            checkAuthStatus(source);
                        }, 5, 5, TimeUnit.SECONDS);
                    });
                } else {
                    String error = responseJson.has("error") ? responseJson.get("error").getAsString() :
                            "Неверный формат ответа (ожидалось поле redirect_url)";
                    client.execute(() -> {
                        source.sendError(Text.literal("§cОшибка API: " + error));
                    });
                }
            } catch (Exception e) {
                System.err.println("Auth error: " + e);
                e.printStackTrace();
                client.execute(() -> {
                    source.sendError(Text.literal("§cОшибка соединения: " + e.getMessage()));
                });
            }
        }).start();
    }

    private static void checkAuthStatus(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Получаем UUID и удаляем дефисы
        String playerUuid = client.player.getUuid().toString().replace("-", "");

        new Thread(() -> {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = API_BASE + "/authorize/check/" + playerUuid; // UUID без дефисов
                System.out.println("Checking auth status at: " + url);

                HttpGet request = new HttpGet(url);
                if (authToken != null) {
                    request.setHeader("Authorization", "Bearer " + authToken);
                }

                String response = httpClient.execute(request, httpResponse -> {
                    int status = httpResponse.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(httpResponse.getEntity());
                    System.out.println("Auth check response: " + status + " - " + body);
                    return body;
                });

                JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();

                if (responseJson.has("token")) {
                    authToken = responseJson.get("token").getAsString();
                    client.execute(() -> {
                        source.sendFeedback(Text.literal("§aАвторизация успешно завершена!"));
                        if (executorService != null) {
                            executorService.shutdown();
                        }
                    });
                } else if (responseJson.has("error")) {
                    String error = responseJson.get("error").getAsString();
                    client.execute(() -> {
                        if (error.equals("not_authorized")) {
                            source.sendError(Text.literal("§cОжидание авторизации через Discord..."));
                        } else {
                            source.sendError(Text.literal("§cОшибка авторизации: " + error));
                            if (executorService != null) {
                                executorService.shutdown();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Auth check error: " + e);
                client.execute(() -> {
                    source.sendError(Text.literal("§cОшибка проверки статуса: " + e.getMessage()));
                });
            }
        }).start();
    }

    private void registerKeyBindings() {
        keyOpenScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_main_screen",
                GLFW.GLFW_KEY_H,
                "category.spmhelper"
        ));

        keyOpenCallsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_calls_screen",
                GLFW.GLFW_KEY_C,
                "category.spmhelper"
        ));

        keyOpenPayScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_pay_screen",
                GLFW.GLFW_KEY_P,
                "category.spmhelper"
        ));

        keyOpenMapScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_map_screen",
                GLFW.GLFW_KEY_M,
                "category.spmhelper"
        ));

        keyOpenLawsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spmhelper.open_laws_screen",
                GLFW.GLFW_KEY_L,
                "category.spmhelper"
        ));
    }

    private void registerKeyHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null) {
                if (keyOpenScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new MainScreen());
                } else if (keyOpenCallsScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new CallsScreen());
                } else if (keyOpenPayScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new PayScreen());
                } else if (keyOpenMapScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new MapScreen());
                } else if (keyOpenLawsScreen.wasPressed()) {
                    MinecraftClient.getInstance().setScreen(new LawsScreen());
                }
            }
        });
    }

}