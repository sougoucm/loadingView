# loadingView
A simple custom control: loadingView

这个loadingView可以在图片下添加文案(加载中...)，还可以自定义动画效果，自定义布局文件，屏蔽用户操作，点击返回键后隐藏。

库类中包含了一个LoadingDialogBase抽象类 ，继承了Dialog类，包含了一个ProgressBar对象，负责基本UI的添加，它还包含了两个方法，onShow()负责给progressBar添加补充动画，getProgressDrawable()返回一个drawable对象，负责显示图片或帧动画。LoadingDialogBase的实现类包括 LoadingDialog和LoadingCrane，LoadingDialog是一个包含了google提供的自定义Drawable 动画的加载框效果的子类。而LoadingCrane是一个利用帧动画实现的加载框效果的子类

![image](https://github.com/sougoucm/loadingView/blob/master/first.gif)
![image](https://github.com/sougoucm/loadingView/blob/master/sec.gif)