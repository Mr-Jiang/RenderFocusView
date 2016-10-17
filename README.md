# RenderFocusView

如果下载或clone下来的代码是乱码，请到如下CSDN进行留言，因为公司安装了文件加密程序，谢谢！
# CSDN--http://blog.csdn.net/jspping/article/details/52837842
# RenderFocusView Button

![image](https://github.com/Mr-Jiang/RenderFocusView/blob/master/RenderFocusView/render/render_button.gif)

# RenderFocusView ListView

![image](https://github.com/Mr-Jiang/RenderFocusView/blob/master/RenderFocusView/render/render_list.gif)

# RenderFocusView GridView

![image](https://github.com/Mr-Jiang/RenderFocusView/blob/master/RenderFocusView/render/render_grid.gif)

# RenderFocusView Project demo

![image](https://github.com/Mr-Jiang/RenderFocusView/blob/master/RenderFocusView/render/render.gif)

# Usage --xml

    <com.example.renderfocusview.RenderFocusView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        app:RenderAlpha="0.2"
        app:RenderDelayClick="false"
        app:RenderHover="true"
        app:RenderOverlay="true" >
        <View>
         in the here add your child view,the view must be extends view
        </View>
    </com.example.renderfocusview.RenderFocusView>

# Usage --java
RenderFocusView.on(View view).RenderColor(int color)).RenderAlpha(float f).RenderHover(boolean true).create();

# Properties --for example
RenderFocusView.on(new TextView(this)).RenderColor(0xff000000 | new Random().nextInt(0x00ffffff)).RenderAlpha(0.2f).RenderHover(true).create();
