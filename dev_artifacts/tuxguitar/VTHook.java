package org.herac.tuxguitar.player.base;

// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;
 
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VTHook {
  // private static FileWriter file;
  private FileWriter intervalFile;
  private JSONObject intervalMap;
  private JSONArray kickIntervals;
  private JSONArray snareIntervals;
  private JSONArray tom1Intervals;
  private JSONArray tom2Intervals;
  private JSONArray tom3Intervals;
  private JSONArray crashIntervals;
  private JSONArray openHiHatIntervals;
  private JSONArray closedHiHatIntervals;
  private Integer count;
  public boolean isRecording = false;
  private static VTHook instance = null;
  private double snareStart;
  private double kickStart;
  private double tom1Start;
  private double tom2Start;
  private double tom3Start;
  private double crashStart;
  private double openHiHatStart;
  private double closedHiHatStart;
  Map<String, Integer> drumKeyMap = new HashMap<String, Integer>();

  public VTHook() {
    System.out.println("vt:VTHook.ctor: entered");

    initDrumKeyMap();

    if (intervalMap == null) {
      System.out.println("VTHook: about to get JSONObject");
      intervalMap = new JSONObject();
    }
    this.count = 0;

    initDrumIntervals();

    System.out.println("vt:VTHook.ctor: count=" + count);

    if(intervalFile == null) {
      try
      {  
        // file = new FileWriter("/vtstuff/tmp/vt_hook.txt");
        // this.intervalFile = new FileWriter("d:\\vtstuff_d\\tmp\\vt_hook.txt", false);
        this.intervalFile = new FileWriter("d:\\vtstuff_d\\github\\cube-test\\resources\\public\\sounds\\rock_candy_intervals.txt", false);
      }
      catch (IOException e) 
      {
        System.out.println("VTHook: cannot create file, file=" + intervalFile);
        e.printStackTrace();
      } 
    }
    // this.init();
  }

  public static VTHook getInstance() {
    if (instance == null) {
        instance = new VTHook();
    }
    return instance;
  }

  // 36: kick
	// 40: snare
	// 42: closed hi-hat
	// 44: open hi-hat
	// 49: crash
  private void initDrumKeyMap() {
    drumKeyMap.put("kick",  36);
    drumKeyMap.put("snare", 38);
    drumKeyMap.put("tom-1", 45);
    drumKeyMap.put("tom-2", 43);
    drumKeyMap.put("tom-3", 41);
    drumKeyMap.put("closed-hi-hat", 42);
    drumKeyMap.put("open-hi-hat", 44);
    drumKeyMap.put("crash", 49);
    drumKeyMap.put("crash-2", 57);
  }

  private void initDrumIntervals() {
    this.kickIntervals = new JSONArray();
    this.snareIntervals = new JSONArray();
    this.tom1Intervals = new JSONArray();
    this.tom2Intervals = new JSONArray();
    this.tom3Intervals = new JSONArray();
    this.crashIntervals = new JSONArray();

    this.intervalMap.put("kick", this.kickIntervals);
    this.intervalMap.put("snare", this.snareIntervals);
    this.intervalMap.put("tom-1", this.tom1Intervals);
    this.intervalMap.put("tom-2", this.tom2Intervals);
    this.intervalMap.put("tom-3", this.tom3Intervals);
    this.intervalMap.put("crash", this.crashIntervals);
  }

  public void startRecording() {
    initTimers();
  }

  private void initTimers() {
    double now = System.currentTimeMillis();
    snareStart = now;
    kickStart = now;
    tom1Start = now;
    tom2Start = now;
    tom3Start = now;
    crashStart = now;
    closedHiHatStart = now;
    openHiHatStart = now;
  }

  public void hookNote(int channelId, int key) {
    isRecording = true;
    double now = System.currentTimeMillis();
    System.out.println("VTHook:hookNote: now=" + now);

    // Drums
    // Note: channelId is song-dependent
    if (channelId == 3) {
      double snareInterval;
      double kickInterval;
      double tom1Interval;
      double tom2Interval;
      double tom3Interval;
      double crashInterval;
      double openHiHatInterval;
      double closedHiHatInterval;

      // // if (key == 38) {
      if (key == drumKeyMap.get("snare")) {
        snareInterval = now - snareStart;
        snareIntervals.put(snareInterval);
        snareStart = now;
      }
      // else if (key == 36) {
      else if (key == drumKeyMap.get("kick")) {
        kickInterval = now - kickStart;
        kickIntervals.put(kickInterval);
        kickStart = now;
      }
      else if (key == drumKeyMap.get("tom-1")) {
        tom1Interval = now - tom1Start;
        tom1Intervals.put(tom1Interval);
        tom1Start = now;
      }
      else if (key == drumKeyMap.get("tom-2")) {
        tom2Interval = now - tom2Start;
        tom2Intervals.put(tom2Interval);
        tom2Start = now;
      }
      else if (key == drumKeyMap.get("tom-3")) {
        tom3Interval = now - tom3Start;
        tom3Intervals.put(tom3Interval);
        tom3Start = now;
      }
      else if (key == drumKeyMap.get("crash") || key == drumKeyMap.get("crash-2")) {
        crashInterval = now - crashStart;
        crashIntervals.put(crashInterval);
        crashStart = now;
      }
      else if (key == drumKeyMap.get("open-hi-hat")) {
        openHiHatInterval = now - openHiHatStart;
        openHiHatIntervals.put(openHiHatInterval);
        openHiHatStart = now;
      }
      else if (key == drumKeyMap.get("closed-hi-hat")) {
        closedHiHatInterval = now - closedHiHatStart;
        closedHiHatIntervals.put(closedHiHatInterval);
        closedHiHatStart = now;
      }
    }
  }

  private void filterByChannelId(int channelId) {

  }

  public void stopRecording() {
    System.out.println("vt:VTHook:stopRecording: entered");
    try
    {
      if (this.intervalFile != null)
      {
        this.intervalFile.write(this.intervalMap.toString());
      }
    }
    catch (IOException e) 
    {
      e.printStackTrace();
    } 
    finally 
    { 
      try {
          this.intervalFile.flush();
          this.intervalFile.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    isRecording = false;
  }

}