package fr.toulousescape.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import fr.toulousescape.ui.IndicesPanel;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class Player {

	private Map<String,Info> _audioOut;

	private String _currentOut;
	
	private boolean _stop;
	
	private boolean _pause;
	
	private byte[] _currentRead;
	
	private Logger logger;

	public Player() {
		logger = LogManager.getLogger(Player.class);
		Info[] audioOut = AudioSystem.getMixerInfo();
		_audioOut = new HashMap<>();
		for (Info audioInfo :  audioOut) {
			if (!audioInfo.getDescription().equals("Port Mixer")
					&& !_audioOut.containsKey(audioInfo.getName())
					&& !audioInfo.getName().contains("Micro"))
			{
				_audioOut.put(audioInfo.getName(), audioInfo);
			}
		}
	}

	public Map<String,Info> getAudioOutList() {
		return _audioOut;
	}
	
	public Set<String> getOutNames() {
		return _audioOut.keySet();
	}

	public void setCurrentOut(String out) {
		_currentOut = out;
	}

	public void play(String url) {
		try {
			logger.debug("Play " + url);
			Mixer mixer = AudioSystem.getMixer(_audioOut.get(_currentOut));
			mixer.open();
			logger.debug("Play 1");
			MpegAudioFileReader mp = new MpegAudioFileReader();
			AudioInputStream audioFileFormat = mp.getAudioInputStream(new File("src\\resources\\" + url));
			logger.debug("Play 2");
			AudioFormat format = audioFileFormat.getFormat();
			logger.debug("Play 2");
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(
							new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16,
									format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false),
							audioFileFormat);
			logger.debug("Play 3");
			AudioFormat audioFormat = audioInputStream.getFormat();
			logger.debug("Play 4");
			javax.sound.sampled.DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			logger.debug("Play 5");
			SourceDataLine line = (SourceDataLine) mixer.getLine(info);
			logger.debug("Play 6");
			
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
			logger.debug("Play 7");
			line.start();
			logger.debug("Started");

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
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
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
