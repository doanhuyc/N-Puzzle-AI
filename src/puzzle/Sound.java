/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package puzzle;

import  sun.audio.*;
import  java.io.*;
import java.net.URL;
/**
 *
 * @author Huy Cuong Doan
 */
public class Sound {
    private AudioData ad;
    private AudioDataStream as;
    private URL url;
    
    public Sound()
    {
        url = null;
        ad = null;
        as = null;
    }
    
    public void play(URL url1)
    {
        this.url = url1;
        try{
            ad = (new AudioStream(url.openStream())).getData();
            as = new AudioDataStream(ad);
            AudioPlayer.player.start(as);
        }catch(Exception e){};
    }
    
    public void stop()
    {
        try{
            AudioPlayer.player.stop(as);
        }catch(IOError e){};
    }   
}
