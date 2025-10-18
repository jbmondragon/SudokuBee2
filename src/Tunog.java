import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Tunog {
	private Clip sample;

	public Tunog(String fname) {
		try {
			File audioFile = new File(fname); // <— fixed here
			if (!audioFile.exists()) {
				System.err.println("Sound file not found: " + audioFile.getAbsolutePath());
				return;
			}

			try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
				sample = AudioSystem.getClip();
				sample.open(audioInputStream);
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (sample != null) {
			sample.stop();
			sample.setFramePosition(0);
			sample.start();
		}
	}

	public void stop() {
		if (sample != null) {
			sample.stop();
		}
	}

	public void loop() {
		if (sample != null) {
			sample.stop();
			sample.setFramePosition(0);
			sample.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
}
