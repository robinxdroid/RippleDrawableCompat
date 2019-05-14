### 简介 ###

RippleDrawable兼容类，同时更方便的创建View点击水波纹效果

### 预览 ###

![](https://github.com/robinxdroid/RippleDrawableCompat/blob/master/1.gif?raw=true)

### Usage ###
```java
//使用{@link RippleDrawableCompat}，从中心位置扩散波纹
RippleDrawableCompat.createCenterRipple(tv1, Color.RED);
```

```java
//LOLLIPOP以上版本使用android.graphics.drawable.RippleDrawable,此时需要为View设置一个背景，用于计算边界.LOLLIPOP以下版本使用RippleDrawableCompat
RippleDrawableCompat.createRipple(tv2, Color.RED);
```

```java
//使用{@link RippleDrawableCompat}，从点击位置扩散波纹
RippleDrawableCompat.createRippleCompat(tv3, Color.RED);
```