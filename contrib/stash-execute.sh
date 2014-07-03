user="${bamboo_ecosystem_username}"
password="${bamboo_ecosystem_password}"
current_branch=${bamboo.repository.branch.name}

sputnik_version="1.1"
stash_host=???
project_key=???
repository_slug=???

echo "current branch is: $current_branch"


url="https://$stash_host/rest/api/1.0/projects/$project_key/repos/$repository_slug/pull-requests"
sputnik_url="https://github.com/TouK/sputnik/releases/download/v$sputnik_version/sputnik-$sputnik_version.zip"

tmp_dir=`mktemp -d`

#############

case "$current_branch" in
    master|production|release*)
        echo "Not checking master or production or release branch, exiting."
        exit
        ;;
esac

tmp_output="$tmp_dir/json"
curl -s $url -u $user:$password > $tmp_output


pyexec() {
    echo "`/usr/bin/python -c 'import sys; exec sys.stdin.read()'`"
}

pullRequestId() {
    pyexec <<END
import json
from pprint import pprint
json_data=open('$tmp_output')

data = json.load(json_data)
json_data.close()

for pr in data["values"]:
    if pr["fromRef"]["displayId"] == "$current_branch":
        print pr["id"]
END
}

prId=`pullRequestId`

## download sputnik
wget -O "$tmp_dir/sputnik.zip" $sputnik_url
cd $tmp_dir
unzip sputnik.zip
cd -

## filter properties file
sed -i -e "s/<username>/$user/; s/<password>/$password/" sputnik.properties

## run code analysis
cd ${bamboo.build.working.directory}
if [ -e "sputnik.properties" ]; then 
$tmp_dir/sputnik-$sputnik_version/bin/sputnik \
    --conf sputnik.properties \
    --pullRequestId $prId

else
  echo "no sputnik.properties present"
fi

## cleanup

rm -R $tmp_dir
