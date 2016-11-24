package cris.connection;

public class TestCRISService {
	
	 public static void main(String[] args) throws Exception {
		 	
		 	// generate access token for the CRIS Service
		 	Authentification auth = new Authentification("780dc1a44749494caaababad10673b8c", "dc72dde014cd4d25a3bb80324d906454");
	    	AccessToken token = auth.GetAccessToken();
	    	
	    	// speech to text
	    	String waveFile = "C:/Users/Roboy/Cognitive-Speech-STT-Windows/samples/SpeechRecognitionServiceExample/batman.wav";
	    	RecognitionService.Recognize(waveFile, token.access_token);
	 }

}
