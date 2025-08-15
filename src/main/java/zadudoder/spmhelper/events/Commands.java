package zadudoder.spmhelper.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import zadudoder.spmhelper.Screen.Calls.ServiceAcceptScreen;
import zadudoder.spmhelper.Screen.Pays.AddCardScreen;
import zadudoder.spmhelper.Screen.Pays.PayScreen;
import zadudoder.spmhelper.config.SPmHelperConfig;
import zadudoder.spmhelper.utils.SPWorldsApi;
import zadudoder.spmhelper.utils.SPmHelperApi;
import zadudoder.spmhelper.utils.types.Service;

public class Commands {
    private static final boolean hasToken = SPmHelperConfig.get().getAPI_TOKEN() != null && !SPmHelperConfig.get().getAPI_TOKEN().isEmpty();

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Главная команда
            var mainCommand = ClientCommandManager.literal("spmhelper")
                    .then(ClientCommandManager.literal("auth")
                            .executes(context -> {
                                SPmHelperApi.getAuthStatus().thenAccept(status -> {
                                    if (status == 200) {
                                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.status_FeedBackMessageCase200"));
                                    } else {
                                        SPmHelperApi.startAuthProcess(context.getSource().getPlayer());
                                    }
                                });
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("status")
                            .executes(context -> {
                                SPmHelperApi.getAuthStatus().thenAccept(status -> {
                                    String message = switch (status) {
                                        case 200 -> "text.spmhelper.status_FeedBackMessageCase200";
                                        case 400 -> "text.spmhelper.status_FeedBackMessageCase400";
                                        case 401 -> "text.spmhelper.status_FeedBackMessageCase401";
                                        default -> "text.spmhelper.status_FeedBackMessageCaseDefault" + status;
                                    };
                                    context.getSource().sendFeedback(Text.translatable(message));
                                });
                                return 1;
                            })
                    ).then(ClientCommandManager.literal("addcard")
                            .then(ClientCommandManager.argument("id", StringArgumentType.string())
                                    .then(ClientCommandManager.argument("token", StringArgumentType.string())
                                            .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                                    .executes(context -> {
                                                        String id = StringArgumentType.getString(context, "id");
                                                        String token = StringArgumentType.getString(context, "token");
                                                        String name = StringArgumentType.getString(context, "name");

                                                        if (SPWorldsApi.getAuthStatus(id, token) == 200) {
                                                            MinecraftClient.getInstance().send(() -> {
                                                                MinecraftClient.getInstance().setScreen(new AddCardScreen(id, token, name));
                                                            });
                                                        } else {
                                                            context.getSource().sendFeedback(Text.translatable("text.spmhelper.TokenOrIdIsIncorrect"));
                                                        }

                                                        return 1;
                                                    }))))
                    );/*.then(ClientCommandManager.literal("tutorial")
                            .then(ClientCommandManager.literal("start")
                                    .executes(context -> {
                                        TutorialManager.startFullTutorial();
                                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.startTutorialMessage"));
                                        return 1;
                                    })
                                    .then(ClientCommandManager.literal("Палатка новичков")
                                            .executes(context -> {
                                                TutorialManager.startPNTutorial();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startPNTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("ЦИК")
                                            .executes(context -> {
                                                TutorialManager.startCIKTutorial();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startCIKTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Банк")
                                            .executes(context -> {
                                                TutorialManager.startBankTutorial();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startBankTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Галерея")
                                            .executes(context -> {
                                                TutorialManager.startGalleryTutorial();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startGalleryTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Суд")
                                            .executes(context -> {
                                                TutorialManager.startLawCourtTutorial();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startLawTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Ад")
                                            .executes(context -> {
                                                TutorialManager.startGoToHell();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startHellTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Энд")
                                            .executes(context -> {
                                                TutorialManager.StartEnd();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startEndTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("ФСБ")
                                            .executes(context -> {
                                                TutorialManager.goToFSB();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startFSBTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Библиотека")
                                            .executes(context -> {
                                                TutorialManager.goToBiblioteka();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startLibraryTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Детективы")
                                            .executes(context -> {
                                                TutorialManager.goToSpawn();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startGoToSpawnTutorialMessage"));
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("Торговля")
                                            .executes(context -> {
                                                TutorialManager.startGoToEndAndTalkAboutTrade();
                                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.startGoToSpawnTutorialMessage"));
                                                return 1;
                                            })))
                            .then(ClientCommandManager.literal("stop")
                                    .executes(context -> {
                                        if (TutorialManager.isEnabled) {
                                            TutorialManager.stopTutorial();
                                            context.getSource().sendFeedback(Text.translatable("text.spmhelper.stopTutorialMessage"));
                                        } else {
                                            context.getSource().sendFeedback(Text.translatable("text.spmhelper.TutorialIsNotEnabledMessage"));
                                        }

                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("skip")
                                    .executes(context -> {
                                        if (TutorialManager.isEnabled) {
                                            context.getSource().sendFeedback(Text.translatable("text.spmhelper.skipTutorialMessage"));
                                            TutorialManager.skipTutorial();
                                        } else {
                                            context.getSource().sendFeedback(Text.translatable("text.spmhelper.TutorialIsNotEnabledMessage"));
                                        }

                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("feedback")
                                    .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.feedback").getString(), StringArgumentType.greedyString())
                                            .executes(context -> {
                                                String comment = StringArgumentType.getString(context, Text.translatable("text.spmhelper.feedback").getString());
                                                CompletableFuture<HttpResponse<String>> responseFuture = SPmHelperApi.sendFeedback(context.getSource().getPlayer().getName().getLiteralString(), comment);
                                                responseFuture.thenApply(response -> {
                                                    if (response.statusCode() == 200) {
                                                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.feedbackIsSended"));
                                                    } else {
                                                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.feedbackSendError"));
                                                    }
                                                    return 1;
                                                }).exceptionally(ex -> {
                                                    context.getSource().sendFeedback(Text.translatable("text.spmhelper.feedbackSendError"));
                                                    return 1;
                                                });
                                                return 1;
                                            })))*/

            var aliasMainCommand = ClientCommandManager.literal("spmh").redirect(mainCommand.build());

            var payCommand = ClientCommandManager.literal("pay")
                    .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForPayment.NickOrNumCard").getString(), StringArgumentType.string())
                            .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForPayment.Amount").getString(), IntegerArgumentType.integer())
                                    .executes(context -> {
                                        String nickname = StringArgumentType.getString(context, Text.translatable("text.spmhelper.argumentForPayment.NickOrNumCard").getString());
                                        int amount = IntegerArgumentType.getInteger(context, Text.translatable("text.spmhelper.argumentForPayment.Amount").getString());
                                        MinecraftClient.getInstance().send(() -> {
                                            MinecraftClient.getInstance().setScreen(new PayScreen(nickname, amount));
                                        });
                                        return 1;
                                    })
                            ).executes(context -> {
                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.notEnteredAmount"));
                                return 1;
                            })
                    ).executes(context -> {
                        context.getSource().sendFeedback(Text.translatable("text.spmhelper.notEnteredNickOrCardNum"));
                        return 1;
                    });


            var deteciveCallCommand = createCallCommand("detective", Service.DETECTIVE);
            var fsbCallCommand = createCallCommand("fsb", Service.FSB);
            var bankerCallCommand = createCallCommand("banker", Service.BANKER);
            var guideCallCommand = createCallCommand("guide", Service.GUIDE);

            dispatcher.register(mainCommand);
            dispatcher.register(aliasMainCommand);

            dispatcher.register(payCommand);

            dispatcher.register(deteciveCallCommand);
            dispatcher.register(fsbCallCommand);
            dispatcher.register(bankerCallCommand);
            dispatcher.register(guideCallCommand);
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> createCallCommand(String commandName, Service service) {
        var callCommand = ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument(Text.translatable("text.spmhelper.argumentForCalls.Comms").getString(), StringArgumentType.greedyString())
                        .executes(context -> {
                            if (!hasToken) {
                                context.getSource().sendFeedback(Text.translatable("text.spmhelper.status_FeedBackMessageCase401"));
                                SPmHelperApi.startAuthProcess(context.getSource().getPlayer());
                                return 0;
                            }
                            String comment = StringArgumentType.getString(context, Text.translatable("text.spmhelper.argumentForCalls.Comms").getString());
                            MinecraftClient.getInstance().send(() -> {
                                MinecraftClient.getInstance().setScreen(new ServiceAcceptScreen(service, comment, context.getSource().getPlayer()));
                            });
                            return 0;
                        })
                )
                .executes(context -> {
                    context.getSource().sendFeedback(Text.translatable("text.spmhelper.enterComment"));
                    return 0;
                });
        return callCommand;
    }
}