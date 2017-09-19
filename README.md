[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImmersiveDetailSample-brightgreen.svg?style=flat)](https://android-arsenal.com/details/3/5934)
# ImmersiveDetailSample
A sample application show how to realize immersive parallax effect header like **Google Play Store**<br>
<br>
![](https://github.com/matrixxun/ImmersiveDetailSample/raw/master/art/demo.gif) ![](https://github.com/matrixxun/ImmersiveDetailSample/raw/master/art/demo01.gif)

# Feature
1. Toolbar quick return.
2. Statusbar&toolbar changes between transparent and solid when gallery visible/invisible.
3. Gallery parallax effect.

# How to use it
1. Your detail Activty's theme should be:

res/values/style.xml:
``` xml
<style name="AppTheme.NoActionBarTransparentStatusBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
</style>
```
and res/values-v21/style.xml:
``` xml
<style name="AppTheme.NoActionBarTransparentStatusBar">
    <item name="windowActionBar">false</item>    
    <item name="windowNoTitle">true</item>   
    <item name="android:windowDrawsSystemBarBackgrounds">true</item>    
    <item name="android:statusBarColor">@android:color/transparent</item>
</style>
```
2. add "LollipopCompatSingleton.translucentStatusBar()" and  "LollipopCompatSingleton.getInstance().fitStatusBarTranslucentPadding()" to Activity onCreate() method:
``` java
protected void onCreate(Bundle savedInstanceState) {    
    LollipopCompatSingleton.translucentStatusBar(this);    
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.xxxx);
    ......
    LollipopCompatSingleton.getInstance().fitStatusBarTranslucentPadding(toolbar, this);
```
3. Add ObservableScrollView to your layout xml.
``` xml
<com.matrixxun.immersivedetail.sample.widget.ObservableScrollView        
    android:id="@+id/scrollview"        
    android:scrollbars="none"        
    android:layout_width="match_parent"        
    android:layout_height="match_parent">
    ......
```
4. initialize immersive feature.
``` java
scrollview.setupImmersiveEffect(getActivity(),imageContainer,toolbar,toolbarColor,toolbarTitle);
```

## Compat
API level 14+ supported

## Example
See example code here on Github. You can also see it live downloading [this apk](https://raw.githubusercontent.com/matrixxun/ImmersiveDetailSample/master/art/app-debug.apk)

License
--------


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
