#!/bin/bash

mkdir server/src/main/resources
rm -rf server/src/main/resources/dist
mkdir server/src/main/resources/dist
cd ui
pnpm install
pnpm run build
cp -r dist/* ../server/src/main/resources/dist
cd ..
mvn -B package --file server/pom.xml
rm ./WallpaperVideo-jar-with-dependencies.jar
cp server/target/WallpaperVideo-jar-with-dependencies.jar ./