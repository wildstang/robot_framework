# Determine the latest available WPILib release, then update build.gradle to match
from requests import get
from urllib.request import urlretrieve
from re import search, sub
from json import load

RELEASES_URL = 'https://github.com/wpilibsuite/vscode-wpilib/releases'
FILE = 'build.gradle'

year = ''
with open('.wpilib/wpilib_preferences.json') as f:
    year = load(f)['projectYear']

res = get(f'{RELEASES_URL}/latest')
if res.status_code == 200:
    new_version = res.url.split('/')[-1][1:]

    build_gradle = ''
    with open(FILE, 'r') as f:
        build_gradle = f.read()

    match = search(year + r'\.[0-9]\.[0-9]', build_gradle)
    if match:
        old_version = match.group()
        if old_version != new_version:
            print(f'Replacing {old_version} with {new_version}')
            with open(FILE, 'w') as f:
                f.write(sub(old_version, new_version, build_gradle))
        else:
            print(f'{new_version} is the latest version')
    else:
        print(f'No version strings found in {FILE}')
        exit(2)
else:
    print('Unable to determine latest version')
    exit(1)
