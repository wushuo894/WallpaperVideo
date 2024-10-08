# WallpaperVideo

### 小红车播放器

Q: 它的作用是什么？ A: 在线播放你下载的壁纸

Q: 适用场景？ A: 需要用nas下载壁纸，并且想要在线预览与检索

Q: 为什么使用它? A: 使用WallpaperEngine时想要观看视频壁纸需要设置为壁纸或右键使用此电脑检索，同时在此电脑中检索时不又不方便关键词的查找与视频的快速预览。我认为此项目完美的解决了这个问题

### 如何使用

#### Docker部署

    docker run -d --name wallpaper-video -v ./video:/video -p 9877:9877 -e PORT="9877" -e VideoCache="FALSE" -e FILE="/video" -e TZ=Asia/Shanghai --restart always wushuo894/wallpaper-video

| 参数         | 作用     | 默认值           |
|------------|--------|---------------|
| PORT       | 端口号    | 9877          |
| VideoCache | 视频缓存   | FALSE         |
| FILE       | 创意工坊位置 | /video        |
| TZ         | 时区     | Asia/Shanghai |

#### 直接运行

`-p --port 端口号 默认 8080`

`-f --file 创意工坊的位置 默认 Z:\SteamLibrary\steamapps\workshop\content\431960`

示例:

    java -jar WallpaperVideo-jar-with-dependencies.jar -p 8080 -f "Z:\SteamLibrary\steamapps\workshop\content\431960"

![1.jpg](https://github.com/wushuo894/WallpaperVideo/raw/master/image/1.jpg)

