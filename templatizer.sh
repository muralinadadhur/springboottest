#!/bin/bash
# This script can be modified to replace the text release-checklist
#echo rpl -R -f -v  "release-checklist" "math-gateway" .
#echo rpl -R -f -v  "ReleaseChecklist" "MathGateway" .
#echo rpl -R -f -v  "release.checklist" "math.gateway" .
#echo rpl -R -f -v  "LearningServices" "Math" .
#echo rpl -R -f -v  "RELEASE_CHECKLIST" "MATH_GATEWAY" .
#echo rpl -R -f -v  "CHANGE ME" "Math Gateway" .
#
#echo "rename and move below files to match class names accordingly"
#find . -type f | grep -i change
if [ $# -lt 3 ];then
  echo Exiting....
  echo
  echo "Usage: $0 <repo-name> <gitlab-group-name> <new-relic-team-name>"
  echo
  echo "Example: $0 edu-profile-verification-service edu1 edu"
  echo
  exit 1
fi

export SED_CMD="sed"

if [ "`uname`" == "Darwin" ]; then
    # regular mac version of sed won't work, use GNU sed.
    export SED_CMD="gsed"
fi

repo=$1
group=$2
team=$3
export DASH_COUNT=`echo ${repo} | grep -o "-" | wc -l`
change_me=`echo ${repo} | awk '{print tolower($1)}'`
required=`echo ${group} | awk '{print tolower($1)}'`
materials=`echo ${team} | awk '{print tolower($1)}'`

#Convert every word first character to uppercase and then remove the hyphens
ReleaseChecklist=`echo ${change_me} | $SED_CMD -e 's/\b\(.\)/\u\1/g' -e 's/-//g'`

#Convert the firstCharacter of the word to lowercase
releaseChecklist=`echo ${ReleaseChecklist} | $SED_CMD -e 's/./\L&/'`

release.checklist=`echo ${change_me} | tr '-' '.'`
release.checklistPkg=`echo ${change_me} | tr '-' '/'`
RELEASE_CHECKLIST=`echo ${change_me} |  $SED_CMD -e 's/-/_/g' -e 's/./\U&/g'`

ReleaseChecklist=$ReleaseChecklist

echo change_me = $change_me
echo ReleaseChecklist = $ReleaseChecklist
echo release.checklist = $release.checklist
echo release.checklistPkg = $release.checklistPkg
echo RELEASE_CHECKLIST = $RELEASE_CHECKLIST
echo ReleaseChecklist = $ReleaseChecklist
echo releaseChecklist = $releaseChecklist

find . -type f -not -path "*/.git*" -not -path "*replace-release-checklist*" | xargs $SED_CMD -i -e "s/release-checklist/$change_me/g" -e "s/ReleaseChecklist/$ReleaseChecklist/g" -e "s/release.checklist/$release.checklist/g" -e "s/RELEASE_CHECKLIST/$RELEASE_CHECKLIST/g" -e "s/ReleaseChecklist/$ReleaseChecklist/g" -e "s/releaseChecklist/$releaseChecklist/g"
find . -type f -not -path "*replace-group-me*" | xargs $SED_CMD -i -e "s/required/$required/g"
find . -type f -not -path "*/.git*" -not -path "*replace-team-me*" | xargs $SED_CMD -i -e "s/materials/$materials/g"

mkdir -p {src/integTest/java/com/chegg,src/main/java/com/chegg,src/test/java/com/chegg}/$release.checklistPkg

mv src/integTest/java/com/chegg/release.checklist/* src/integTest/java/com/chegg/$release.checklistPkg/
mv src/main/java/com/chegg/release.checklist/* src/main/java/com/chegg/$release.checklistPkg/
mv src/test/java/com/chegg/release.checklist/* src/test/java/com/chegg/$release.checklistPkg/

find . -type f -name "*ReleaseChecklist*"|while read fname; do
    newfname=`echo $fname | $SED_CMD -e "s/ReleaseChecklist/$ReleaseChecklist/g"`
    mv $fname $newfname
done

./update-dependencies.sh
