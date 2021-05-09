# artnetApp

## docker メモ

```
./gradlew jibDockerBuild
```

その後、

```
docker run -it --rm -p 8080:8080 -v [リポジトリのディレクトリ]:/data -t artnet-app /data
```

すると docker 上で動きます。はい。多分。
