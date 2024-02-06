Remove-Item server/src/main/resources/dist -Recurse -Force
New-Item "server/src/main/resources/dist" -ItemType "directory"
cd ui
pnpm run build
cp -r dist/* ../server/src/main/resources/dist
cd ..
mvn -B package --file server/pom.xml
cp server/target/WallpaperVideo-jar-with-dependencies.jar ./