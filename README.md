# DyLoad
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

One kind of Android Plugin Framework

## Project Structure
1.DyDemo
This is an demo module which compile with library module dyloadlib.

2.dyloadlib
This is the core module of DyLoad which can run plugins(for example:plugindemo) without installing them.

3.plugindemo
This is the demo of plugin package.

### File Directions
:dyloadlib/src/main/assets/pluginpkg
This is the apk of app module "plugindemo". For convenience, I put it in the directory of assets.


## Usage

Simply run

execute following command in console

    $ ./gradlew plugindemo:buildFile

then run "DyDemo"(Alt+Shift+X)
