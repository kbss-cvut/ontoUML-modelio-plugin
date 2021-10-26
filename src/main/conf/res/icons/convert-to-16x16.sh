
Field_Separator=$IFS
IFS=,
cd ontouml
types="kind,subkind,role,phase,mixin,role-mixin,phase-mixin,category,relator-type,mode-type,quality-type"
for TYPE in $types
do
  echo $TYPE
  convert ${TYPE}_48.png -resize 16x16 ${TYPE}_16.png
done
cd ..

cd ontoumlplus
types="object-type,event-type,aspect-type"
for TYPE in $types
do
  echo $TYPE
  convert ${TYPE}_48.png -resize 16x16 ${TYPE}_16.png
done

IFS=$Field_Separator
