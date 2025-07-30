package zadudoder.spmhelper.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import zadudoder.spmhelper.SPmHelper;

public class SoundManager {
    MinecraftClient client = MinecraftClient.getInstance();
    public static final SoundEvent start_tutorial_audio = registerSoundEvent("start_tutorial_audio");

    public static void initialize() {
        // Можно добавить инициализацию дополнительных звуков здесь
    }

    /*
    Пример воспроизведния звука

    client.getSoundManager().play(
        PositionedSoundInstance.master(
            SoundManager.FisrtSound,
            1.0f,
            1.0f
        )
    );

    Где нужно обрабатывать звуки:

    Online Audio Converter (https://online-audio-converter.com/)
    Расширение - OGG, 44100 Hz, Моно, Качество 128 kbps

    */

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(SPmHelper.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void playSound(SoundEvent sound, float volume, float pitch) {
        MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(sound, volume, pitch)
        );
    }

    public static void stopSound(SoundEvent sound) {
        MinecraftClient.getInstance().getSoundManager().stop(
                PositionedSoundInstance.master(sound, 1.0f, 1.0f)
        );
    }
}