package org.cityu.os.testui;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
import android.view.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by Hubery on 2017/8/12.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class LoopUITest {

    private static final String CHROME = "com.android.chrome";
    private static final String YOUTUBE = "com.google.android.youtube";
    private static final String FACEBOOK = "com.facebook.katana";
    private static final String TWITTER = "com.twitter.android";
    private static final String WECHAT = "com.tencent.mm";
    private static final String GMAIL = "com.google.android.gm";
    private static final String EARTH = "com.google.earth";
    private static final String INSTAGRAM = "com.instagram.android";
    private static final String GALLERY = "com.google.android.apps.photos";
    private static final String MESSENGER = "com.facebook.orca";

    private UiDevice mDevice;

    Instrumentation instrumentation;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();


    @Test
    @RepeatRule.Repeat(times = 50)
    public void mainTest(){

        instrumentation = InstrumentationRegistry.getInstrumentation();
        mDevice = UiDevice.getInstance(instrumentation);
        try {

            new testTwitter(mDevice,instrumentation,TWITTER).doTest();
            new testEarth(mDevice,instrumentation,EARTH).doTest();
            new testGmail(mDevice,instrumentation,GMAIL).doTest();
            new testFacebook(mDevice,instrumentation,FACEBOOK).doTest();
            new testMessenger(mDevice,instrumentation,MESSENGER).doTest();
            new testInstagram(mDevice,instrumentation,INSTAGRAM).doTest();
            new testChrome(mDevice,instrumentation,CHROME).doTest();
            new testCamera(mDevice,instrumentation,"").doTest();
            new startE4defrag(mDevice, instrumentation, "org.cityu.os.ext4agingsimulator").doTest();
            //new deletePhotos(mDevice, instrumentation, GALLERY).doTest();

            stopApps(CHROME,FACEBOOK,TWITTER,GMAIL,EARTH,INSTAGRAM,MESSENGER);
        }catch (Exception e){
            System.out.println(e.getCause());
        }



    }

    private void stopApps(String ... appPkgs){
        try {
            for(String app : appPkgs){
                mDevice.executeShellCommand("am force-stop " + app);
                SystemClock.sleep(200);
            }
        }catch (Exception e){
            System.out.println(e.getCause());
        }

    }

    private static abstract class TestTask{

        protected String mPackage;
        protected Instrumentation instrumentation;
        protected UiDevice mDevice;
        private long start;

        public TestTask(UiDevice device, Instrumentation instrumentation, String mPackage) {
            this.mDevice = device;
            this.instrumentation = instrumentation;
            this.mPackage = mPackage;
        }

        public void before(){
            mDevice.pressHome();
            start = System.currentTimeMillis();
            Logger("------------------------begin(" + mPackage + ")------------------------------");
        }

        public void after(){
            Logger("costs : " + (System.currentTimeMillis() - start));
            Logger("------------------------end(" + mPackage + ")------------------------------");
        }

        protected void Logger(String string){
            System.out.println("TestUiLogger-------------->>>>>>>>>>>>>>>>   " + string);
        }

        public void openApp() {
            Logger("打开 " + mPackage);
            Context context = instrumentation.getContext();
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(mPackage);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        protected void customSwipe(int count, int t){

            int t1 = count;
            for (int i = 0; i < t1; i++) {
                mDevice.swipe(268, 1200, 268, 500, 100);
                SystemClock.sleep(t);
            }

            SystemClock.sleep(t);

            for (int i = 0; i < t1 + 2; i++) {
                mDevice.swipe(268, 500, 268, 1200, 100);
                SystemClock.sleep(t);
            }
        }

        public UiObject2 findObject(BySelector selector) throws InterruptedException {
            UiObject2 object = null;
            int timeout = 30000;
            int delay = 1000;
            long time = System.currentTimeMillis();
            while (object == null) {
                object = mDevice.findObject(selector);
                SystemClock.sleep(delay);
                if (System.currentTimeMillis() - timeout > time) {
                    break;
                }
            }
            return object;
        }

        public void openCamera(){
            Logger("launch " + "camera");
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            Intent intent = new Intent(); 
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.media.action.STILL_IMAGE_CAMERA");//
            context.startActivity(intent);
        }

        public void openAlbum(){
            Logger("open " + "gallery");
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            Intent intent = new Intent(Intent.ACTION_PICK, null); 
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            context.startActivity(intent);
        }

        public  void longClick(UiDevice ud, UiObject2 uiObject,int steps) throws UiObjectNotFoundException {
            ud.swipe(uiObject.getVisibleBounds().centerX(), uiObject.getVisibleBounds().centerY(),
                    uiObject.getVisibleBounds().centerX(), uiObject.getVisibleBounds().centerY(), steps);
        }

        public String repeatStr(String str, long count){
            String s = "";
            for (int i = 0; i < count; i++) {
                s += str;
            }
            return s;
        }

        public void keyboard(String str){
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if(c >= 'a' && c <= 'z'){
                    mDevice.pressKeyCode(KeyEvent.KEYCODE_A + (c - 'a'));
                }
                if(c >= 'A' && c <= 'Z'){
                    mDevice.pressKeyCode(KeyEvent.KEYCODE_A + (c - 'A'));
                }
                if(c >= '0' && c <= '9'){
                    mDevice.pressKeyCode(KeyEvent.KEYCODE_0 + (c - '0'));
                }
                SystemClock.sleep(30);
            }
        }

        public void doTest(){
            before();
            testContent();
            after();
        }

        public abstract void testContent();



    }

    private static class testFacebook extends TestTask{

        public testFacebook(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            try {

                SystemClock.sleep(2000);
                openApp();
                SystemClock.sleep(5000);

                for (int i = 0; i < 30; i++) {
                    mDevice.swipe(268, 1200, 268, 500, 100);
                    if(i > 5){
                        if(Math.random() < 0.3){
                            SystemClock.sleep(100);
                            mDevice.click(540,860);
                            SystemClock.sleep(2000);
                            mDevice.pressBack();
                        }
                    }
                    SystemClock.sleep(100);
                }

                SystemClock.sleep(100);
               
                for (int i = 0; i < 30; i++) {
                    mDevice.swipe(268, 500, 268, 1200, 100);
                    SystemClock.sleep(100);
                }

                customSwipe(30,100);

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }
        }

    }

    private static class testTwitter extends TestTask{

        public testTwitter(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {

            try {

                openApp();
                Logger("opening twitter");
                mDevice.waitForWindowUpdate("",2000);
                UiObject2 u1 = findObject(By.res("com.twitter.android:id/composer_write"));
                u1.click();
                mDevice.waitForWindowUpdate("",1000);
                UiObject2 u2 = findObject(By.res("com.twitter.android:id/tweet_text"));
                u2.clear();
                u2.setText("test twitter...");
                mDevice.waitForWindowUpdate("",1000);
                UiObject2 u3 = findObject(By.res("com.twitter.android:id/gallery"));
                u3.click();
                mDevice.waitForWindowUpdate("",1000);
                //launch camera
                UiObject2 u4 = findObject(By.res("com.twitter.android:id/image"));
                u4.click();
                mDevice.waitForWindowUpdate("",3000);

                UiObject2 camera = findObject(By.res("com.twitter.android:id/image_camera_shutter"));
                camera.click();

                mDevice.waitForWindowUpdate("",1500);
                UiObject2 u5 = findObject(By.res("com.twitter.android:id/speed_bump_use"));
                u5.click();
                mDevice.waitForWindowUpdate("",500);
                UiObject2 u6 = findObject(By.res("com.twitter.android:id/composer_post"));
                u6.click();
                mDevice.waitForWindowUpdate("",2000);

                customSwipe(30,40);
                //customSwipe();

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }
        }

    }

    private static class testMessenger extends TestTask{

        public testMessenger(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {

            try {

                Log.i("Test", "opening messenger");
                openApp();

                UiObject2 wb = findObject(By.res("com.facebook.orca:id/orca_home_fab"));
                wb.click();

                mDevice.waitForWindowUpdate("",1000);

                List<UiObject2> uiObjs = mDevice.findObjects(By.res("com.facebook.orca:id/contact_picker_list_item"));
                uiObjs.get(0).click();

                mDevice.waitForWindowUpdate("",1000);


                UiObject2 text = findObject(By.res("com.facebook.orca:id/text_input_bar"));
                text.clear();
                SystemClock.sleep(500);
                text.setText(repeatStr("Text From CityU 333333",600));

                UiObject2 send = findObject(By.res("com.facebook.orca:id/composer_send_action_button"));

                send.click();

                SystemClock.sleep(2000);

                UiObject2 camera = findObject(By.res("com.facebook.orca:id/composer_camera_action_button"));
                camera.click();

                mDevice.waitForWindowUpdate("",1000);

                UiObject2 capture = findObject(By.res("com.facebook.orca:id/capture_button"));
                capture.click();

                SystemClock.sleep(1000);

                UiObject2 send2 = findObject(By.res("com.facebook.orca:id/send_button"));

                send2.click();

                SystemClock.sleep(3000);

                //delete records

                List<UiObject2> messeges = mDevice.findObjects(By.res("com.facebook.orca:id/messages_list"));
                System.out.println("-------->>>" + messeges.size());
                longClick(mDevice,messeges.get(0),40);

                SystemClock.sleep(4000);

                List<UiObject2> reactions = mDevice.findObjects(By.res("com.facebook.orca:id/message_reactions_shortcuts_container"));
                List<UiObject2> rs = reactions.get(0).findObjects(By.clazz("android.widget.LinearLayout"));
                rs.get(2).longClick();

                UiObject2 delete_button = findObject(By.res("com.facebook.orca:id/button1"));
                delete_button.click();

                SystemClock.sleep(2000);

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }

        }

    }

    private static class testGmail extends TestTask{

        public testGmail(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {

            try {

                openApp();

                UiObject2 u1 = findObject(By.res("com.google.android.gm:id/compose_button"));
                u1.click();
                mDevice.waitForWindowUpdate("",1000);//write emails

                UiObject2 u2 = findObject(By.res("com.google.android.gm:id/to"));
                u2.clear();
                u2.setText("cityus002@gmail.com");

                UiObject2 u3 = findObject(By.res("com.google.android.gm:id/subject"));
                u3.clear();
                u3.setText("Test Title");

                SystemClock.sleep(500);

                UiObject2 u4 = findObject(By.res("com.google.android.gm:id/body_wrapper"));
                u4.click();

                SystemClock.sleep(500);

                keyboard("Test1000");

                //mDevice.pressKeyCode(KeyEvent.KEYCODE_ENTER);

                UiObject2 u5 = findObject(By.res("com.google.android.gm:id/send"));
                u5.click();

                SystemClock.sleep(1000);

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }

        }



    }

    private static class testCamera extends TestTask{

        public testCamera(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            try {

                openCamera();
                SystemClock.sleep(5000);
                for (int i = 0; i < 50; i++) {
                    System.out.println("-------------> " + i);
                    mDevice.pressKeyCode(KeyEvent.KEYCODE_CAMERA);
                    SystemClock.sleep(200);
                }

                mDevice.pressKeyCode(KeyEvent.KEYCODE_BACK);

            }catch (Exception e){

                System.out.println(e.getCause());
                mDevice.pressKeyCode(KeyEvent.KEYCODE_BACK);

            }
        }

    }

    private static class testEarth extends TestTask{

        public testEarth(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            try {

                openApp();
                SystemClock.sleep(15000);
                UiObject2 g = findObject(By.res("com.google.earth:id/toolbar_feeling_lucky"));

                for (int i = 0; i < 20; i++) {
                    g.click();
                    SystemClock.sleep(13 * 1000);
                }

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }
        }

    }

    private static class testChrome extends TestTask{

        public testChrome(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            try{
                openApp();
                SystemClock.sleep(500);
                UiObject2 urlText = findObject(By.res("com.android.chrome:id/url_bar"));
                urlText.click();
                SystemClock.sleep(500);
            }catch (Exception e){
                Logger(e.getLocalizedMessage());
            }

        }
    }

    private static class testInstagram extends TestTask{

        public testInstagram(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {

            try {

                openApp();

                mDevice.waitForWindowUpdate("",1500);

                UiObject2 camera = findObject(By.pkg("com.instagram.android").clazz("android.widget.ImageView").desc("Camera"));

                camera.click();

                mDevice.waitForWindowUpdate("",2000);

                UiObject2 camera_click = findObject(By.res("com.instagram.android:id/prior_shutter_icon"));
                camera_click.click();

                mDevice.waitForWindowUpdate("",1500);

                UiObject2 next = findObject(By.res("com.instagram.android:id/recipients_picker_button"));
                next.click();

                mDevice.waitForWindowUpdate("",1500);

                UiObject2 check = findObject(By.res("com.instagram.android:id/row_add_to_story_checkbox"));
                check.click();

                mDevice.waitForWindowUpdate("",1500);

                UiObject2 send = findObject(By.res("com.instagram.android:id/button_send"));
                send.click();

                customSwipe(30,100);

            }catch (Exception e){

                Logger(e.getLocalizedMessage());

            }

        }
    }

    private static class startE4defrag extends TestTask{

        public startE4defrag(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            try {
                openApp();
                UiObject2 object = findObject(By.res("org.cityu.os.ext4agingsimulator:id/button_e4defrage"));
                object.click();
                SystemClock.sleep(1000 * 150);
            }catch (Exception e){
                Logger(e.getMessage());
            }

        }

    }

    private static class deletePhotos extends TestTask{

        public deletePhotos(UiDevice device, Instrumentation instrumentation, String mPackage) {
            super(device, instrumentation, mPackage);
        }

        @Override
        public void testContent() {
            openApp();
            SystemClock.sleep(5000);
            UiScrollable scrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
            try {

            /*    for (int i = 0; i < 50; i++) {
                    mDevice.swipe(500,1440,500,440,5);
                }*/
                scrollable.flingToEnd(2);
                //Logger("is null = " + (b));
            } catch (Exception e) {
                Logger(e.getLocalizedMessage());
            }
        }

    }






}
