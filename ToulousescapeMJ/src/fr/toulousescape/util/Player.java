package fr.toulousescape.util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class Player {

	private Info[] _audioOut;

	private int _currentOut;
	
	private boolean _stop;
	
	private boolean _pause;
	
	private byte[] _currentRead;

	public Player() {
		_audioOut = AudioSystem.getMixerInfo();
	}

	public Info[] getAudioOutList() {
		return _audioOut;
	}

	public void setCurrentOut(int out) {
		_currentOut = out;
	}

	public void play(String url) {
		try {
			
			Mixer mixer = AudioSystem.getMixer(_audioOut[_currentOut]);
			mixer.open();
			
			MpegAudioFileReader mp = new MpegAudioFileReader();
			AudioInputStream audioFileFormat = mp.getAudioInputStream(new File("src\\resources\\" + url));
			AudioFormat format = audioFileFormat.getFormat();
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(
							new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16,
									format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false),
							audioFileFormat);
			AudioFormat audioFormat = audioInputStream.getFormat();
			javax.sound.sampled.DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine line = (SourceDataLine) mixer.getLine(info);
			
//			FloatControl control = (FloatControl) line
//            .getControl(FloatControl.Type.BALANCE);
			
			if (line.isControlSupported(BooleanControl.Type.MUTE)) {
//	            muteControl = (BooleanControl) line
//	                    .getControl(BooleanControl.Type.MUTE);
				System.out.println("MUTE");
	        }
	        if (line.isControlSupported(FloatControl.Type.VOLUME)) {
//	            masterGainControl = (FloatControl) line
//	                    .getControl(FloatControl.Type.MASTER_GAIN);
	        	System.out.println("GAIN");
	        }
	        if (line.isControlSupported(FloatControl.Type.PAN)) {
//	            panControl = (FloatControl) line.getControl(FloatControl.Type.PAN);
	        	System.out.println("PAN");
	        }
	        if (line.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
//	            sampleRateControl = (FloatControl) line
//	                    .getControl(FloatControl.Type.SAMPLE_RATE);
	        	System.out.println("RATE");

	        }
			
//			System.out.println(control.getValue());
			
			line.open(audioFormat);
			line.start();

			byte bytes[] = new byte[1024];
			
//			if (_pause)
//			{
//				bytes = _currentRead;
//				_pause = false;
//			}
			
			if (!_pause) {
				_stop = false;
				int bytesRead = 0;
				while (((bytesRead = audioInputStream.read(bytes, 0, bytes.length)) != -1) && !_stop) 
				{
					if (!_stop){
						line.write(bytes, 0, bytesRead);
					}
				}
			}

			_stop = false;
			_pause = false;
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void stop()
	{
		_stop = true;
	}

	public FloatControl getMasterGainControl() {
		// TODO Auto-generated method stub
		return getMasterGainControl();
	}
	
	public void pause()
	{
		_pause = true;
	}
}
