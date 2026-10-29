MS Editor Media Hub - Logo assets

1) ZIP ko extract karein.
2) Is ZIP ke drawable-nodpi aur mipmap folders ke andar wali files ko apne Android project ke:
   app/src/main/res/
   ke matching folders mein copy karein. Agar folders nahi hain to bana sakte hain.
3) AndroidManifest.xml mein <application ...> tag ke andar ye attributes set karein:
   android:icon="@mipmap/ic_launcher"
   android:roundIcon="@mipmap/ic_launcher"
   (Baaki existing attributes ko delete na karein.)
4) App ki Home screen ke upar logo dikhane ke liye layout/code mein:
   R.drawable.ms_editor_logo
   image resource use karein.

Note: Ye ZIP sirf image assets deta hai; MainActivity.kt ko update karna alag step hai.
Existing Supabase data/settings ko is ZIP se koi change nahi hota.
