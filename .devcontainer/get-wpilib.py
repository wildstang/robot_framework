# Determine the latest available WPILib release, then download the corresponding extension
from requests import get
from urllib.request import urlretrieve

RELEASES_URL = 'https://github.com/wpilibsuite/vscode-wpilib/releases'
EXTENSION_PATH = '/tmp/wpilib.vsix'

res = get(f'{RELEASES_URL}/latest')
if res.status_code == 200:
    version = res.url.split('/')[-1][1:]
    extension_url = f'{RELEASES_URL}/download/v{version}/vscode-wpilib-{version}.vsix'

    print('Downloading:', extension_url, 'to', EXTENSION_PATH)
    urlretrieve(extension_url, EXTENSION_PATH)
