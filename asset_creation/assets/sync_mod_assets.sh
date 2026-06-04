#!/usr/bin/env sh
fail() {
  printf "Failed to execute. %s\n" "$1"
  exit 1
}

cd "$(dirname "$0")" || fail 1

name=create_mechanically_enhanced

echo "$PWD/$name"
[ -e "./$name" ] && rm -rI ./"$name"
mkdir $name
cp -r ../../src/generated/resources/assets/$name/* $name
cp -r ../../src/main/resources/assets/$name/* $name
