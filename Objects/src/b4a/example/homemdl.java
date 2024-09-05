package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class homemdl extends Activity implements B4AActivity{
	public static homemdl mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new anywheresoftware.b4a.ShellBA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.homemdl");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (homemdl).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.homemdl");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.homemdl", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (homemdl) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (homemdl) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return homemdl.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (homemdl) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (homemdl) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            homemdl mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (homemdl) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }



public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblend = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollview1 = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.startmdl _startmdl = null;
public b4a.example.readymdl _readymdl = null;
public b4a.example.signinmdl _signinmdl = null;
public b4a.example.usermdl _usermdl = null;
public b4a.example.gamesmdl _gamesmdl = null;
public b4a.example.newmdl _newmdl = null;
public b4a.example.fastmdl _fastmdl = null;
public b4a.example.downloadmdl _downloadmdl = null;
public b4a.example.categoriesmdl _categoriesmdl = null;
public b4a.example.profilesmdl _profilesmdl = null;
public b4a.example.searchmdl _searchmdl = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}));}
RDebugUtils.currentLine=2686976;
 //BA.debugLineNum = 2686976;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
RDebugUtils.currentLine=2686978;
 //BA.debugLineNum = 2686978;BA.debugLine="Activity.LoadLayout(\"HomeLay\")";
mostCurrent._activity.LoadLayout("HomeLay",mostCurrent.activityBA);
RDebugUtils.currentLine=2686979;
 //BA.debugLineNum = 2686979;BA.debugLine="ScrollView1.Panel.LoadLayout(\"HsvHomeContent\")";
mostCurrent._scrollview1.getPanel().LoadLayout("HsvHomeContent",mostCurrent.activityBA);
RDebugUtils.currentLine=2686980;
 //BA.debugLineNum = 2686980;BA.debugLine="ScrollView1.Panel.Height = LblEnd.Top";
mostCurrent._scrollview1.getPanel().setHeight(mostCurrent._lblend.getTop());
RDebugUtils.currentLine=2686982;
 //BA.debugLineNum = 2686982;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="homemdl";
RDebugUtils.currentLine=2818048;
 //BA.debugLineNum = 2818048;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=2818050;
 //BA.debugLineNum = 2818050;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=2752512;
 //BA.debugLineNum = 2752512;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=2752514;
 //BA.debugLineNum = 2752514;BA.debugLine="End Sub";
return "";
}
public static String  _imageview2_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "imageview2_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "imageview2_click", null));}
RDebugUtils.currentLine=3407872;
 //BA.debugLineNum = 3407872;BA.debugLine="Private Sub ImageView2_Click";
RDebugUtils.currentLine=3407873;
 //BA.debugLineNum = 3407873;BA.debugLine="StartActivity(ProfilesMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._profilesmdl.getObject()));
RDebugUtils.currentLine=3407874;
 //BA.debugLineNum = 3407874;BA.debugLine="End Sub";
return "";
}
public static String  _imgdownload_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "imgdownload_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "imgdownload_click", null));}
RDebugUtils.currentLine=3276800;
 //BA.debugLineNum = 3276800;BA.debugLine="Private Sub imgDownload_Click";
RDebugUtils.currentLine=3276801;
 //BA.debugLineNum = 3276801;BA.debugLine="StartActivity(DownloadMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._downloadmdl.getObject()));
RDebugUtils.currentLine=3276802;
 //BA.debugLineNum = 3276802;BA.debugLine="End Sub";
return "";
}
public static String  _imgfast_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "imgfast_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "imgfast_click", null));}
RDebugUtils.currentLine=3145728;
 //BA.debugLineNum = 3145728;BA.debugLine="Private Sub imgFast_Click";
RDebugUtils.currentLine=3145729;
 //BA.debugLineNum = 3145729;BA.debugLine="StartActivity(FastMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._fastmdl.getObject()));
RDebugUtils.currentLine=3145730;
 //BA.debugLineNum = 3145730;BA.debugLine="End Sub";
return "";
}
public static String  _imggames_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "imggames_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "imggames_click", null));}
RDebugUtils.currentLine=2883584;
 //BA.debugLineNum = 2883584;BA.debugLine="Private Sub imgGames_Click";
RDebugUtils.currentLine=2883585;
 //BA.debugLineNum = 2883585;BA.debugLine="StartActivity(GamesMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._gamesmdl.getObject()));
RDebugUtils.currentLine=2883586;
 //BA.debugLineNum = 2883586;BA.debugLine="End Sub";
return "";
}
public static String  _imgnew_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "imgnew_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "imgnew_click", null));}
RDebugUtils.currentLine=3014656;
 //BA.debugLineNum = 3014656;BA.debugLine="Private Sub imgNew_Click";
RDebugUtils.currentLine=3014657;
 //BA.debugLineNum = 3014657;BA.debugLine="StartActivity(NewMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._newmdl.getObject()));
RDebugUtils.currentLine=3014658;
 //BA.debugLineNum = 3014658;BA.debugLine="End Sub";
return "";
}
public static String  _label4_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "label4_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "label4_click", null));}
RDebugUtils.currentLine=3538944;
 //BA.debugLineNum = 3538944;BA.debugLine="Private Sub Label4_Click";
RDebugUtils.currentLine=3538945;
 //BA.debugLineNum = 3538945;BA.debugLine="StartActivity(CategoriesMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._categoriesmdl.getObject()));
RDebugUtils.currentLine=3538946;
 //BA.debugLineNum = 3538946;BA.debugLine="End Sub";
return "";
}
public static String  _lbldownloads_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lbldownloads_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lbldownloads_click", null));}
RDebugUtils.currentLine=3342336;
 //BA.debugLineNum = 3342336;BA.debugLine="Private Sub LblDownloads_Click";
RDebugUtils.currentLine=3342337;
 //BA.debugLineNum = 3342337;BA.debugLine="StartActivity(DownloadMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._downloadmdl.getObject()));
RDebugUtils.currentLine=3342338;
 //BA.debugLineNum = 3342338;BA.debugLine="End Sub";
return "";
}
public static String  _lblfast_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblfast_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblfast_click", null));}
RDebugUtils.currentLine=3211264;
 //BA.debugLineNum = 3211264;BA.debugLine="Private Sub LblFast_Click";
RDebugUtils.currentLine=3211265;
 //BA.debugLineNum = 3211265;BA.debugLine="StartActivity(FastMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._fastmdl.getObject()));
RDebugUtils.currentLine=3211266;
 //BA.debugLineNum = 3211266;BA.debugLine="End Sub";
return "";
}
public static String  _lblgames_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblgames_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblgames_click", null));}
RDebugUtils.currentLine=2949120;
 //BA.debugLineNum = 2949120;BA.debugLine="Private Sub LblGames_Click";
RDebugUtils.currentLine=2949121;
 //BA.debugLineNum = 2949121;BA.debugLine="StartActivity(GamesMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._gamesmdl.getObject()));
RDebugUtils.currentLine=2949122;
 //BA.debugLineNum = 2949122;BA.debugLine="End Sub";
return "";
}
public static String  _lblnew_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblnew_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblnew_click", null));}
RDebugUtils.currentLine=3080192;
 //BA.debugLineNum = 3080192;BA.debugLine="Private Sub LblNew_Click";
RDebugUtils.currentLine=3080193;
 //BA.debugLineNum = 3080193;BA.debugLine="StartActivity(NewMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._newmdl.getObject()));
RDebugUtils.currentLine=3080194;
 //BA.debugLineNum = 3080194;BA.debugLine="End Sub";
return "";
}
public static String  _lblsearch_click() throws Exception{
RDebugUtils.currentModule="homemdl";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblsearch_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblsearch_click", null));}
RDebugUtils.currentLine=3473408;
 //BA.debugLineNum = 3473408;BA.debugLine="Private Sub LblSearch_Click";
RDebugUtils.currentLine=3473409;
 //BA.debugLineNum = 3473409;BA.debugLine="StartActivity(SearchMdl)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._searchmdl.getObject()));
RDebugUtils.currentLine=3473410;
 //BA.debugLineNum = 3473410;BA.debugLine="End Sub";
return "";
}
}