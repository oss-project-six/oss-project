#!/bin/bash

cd /home/ubuntu/app || exit 1

echo "🔁 기존 프로세스 종료 중..."
pkill -f 'java -jar' || true

echo "🚀 새 JAR 실행 중..."
nohup java -jar $(ls -t *.jar | head -n 1) > app.log 2>&1 &

sleep 2

echo "✅ 배포 완료. 현재 실행 중인 Java 프로세스:"
ps aux | grep java | grep -v grep
