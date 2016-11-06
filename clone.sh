#!/bin/bash

#1. Set variables
project_name="DNDCharacterGenerator"
lowercase_name="dndchargen"
dndchargen_directory=$(pwd)

#2. Make new dir and copy dndchargen into it
cd ..
if [ -d "$project_name" ]; then
  ECHO "PROJECT DIRECTORY EXISTS - EXITING"
  exit
fi
 
mkdir $project_name
cp -r $dndchargen_directory/. $project_name

cd $project_name

#3. remove things that shouldn't be cloned
rm -rf .git
rm -rf voice-interface
rm -rf ./service/target

#4. Rename files
mv ./dndchargen.json ./$lowercase_name".json"
mv ./dndchargen_local.json ./$lowercase_name"_local.json"
mv ./service/src/main/java/com/derpgroup/dndchargen ./service/src/main/java/com/derpgroup/$lowercase_name
mv ./service/src/test/java/com/derpgroup/dndchargen ./service/src/test/java/com/derpgroup/$lowercase_name

#5. Rename packages
find . -type f -exec sed -i "s/dndchargen/"$lowercase_name"/g" {} +
find . -type f -exec sed -i "s/DNDCharacterGenerator/"$project_name"/g" {} +

#6. Self-destruct this clone file?