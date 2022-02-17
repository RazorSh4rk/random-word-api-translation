#!/bin/bash

sudo pacman -S docker scala sbt

# start a libretranslate instance locally
docker run -ti --rm -p 5000:5000 libretranslate/libretranslate

# replace "es" with your language of choice
# https://libretranslate.com/languages
sbt "run es"
