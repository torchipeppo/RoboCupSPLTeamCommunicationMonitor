# Deals medium Literature-type damage to all enemies :P
# requires python 3.11, use conda in case

from pathlib import Path
import subprocess
from contextlib import chdir

TEAM_IDs = {
    "Bembelbots": 3,
    "B-Human": 5,
    "Nao-Devils": 12,
    "HTWK-Robots": 13,
    "SPQR-Team": 19,
    "HULKs": 24,
    "NomadZ": 33,
}

GOOD_FOR_EGO = {"Nao-Devils", "B-Human", "HTWK-Robots"}
GOOD_FOR_OPP = {"HULKs", "SPQR-Team", "NomadZ", "Bembelbots"}.union(GOOD_FOR_EGO)

BASE_DIRECTORY = Path(__file__).resolve().parent
LOGDIR = BASE_DIRECTORY.parent / "RoboCup2023Logs"
BINDIR = BASE_DIRECTORY / "bin"

def do(fpath, egoteam, oppteam):
    # TODO NEXT filtrare per ego, in qualche modo. anche togliendo sta doppia chiamata
    # e facendo tipo "fai csv, poi smista le righe in altri due csv, tieni solo gli ego-team GOOD_FOR_EGO"
    cmdlist = ["java", "-jar", "TeamCommunicationMonitor.jar", "-t",  str(fpath)]
    print("Running", " ".join(cmdlist))
    with chdir(BINDIR):
        subprocess.run(cmdlist)

for fpath in LOGDIR.rglob("*.yaml"):
    _log, _date, _time, team1, team2 = fpath.stem.split("_")
    if (team1 in GOOD_FOR_EGO and team2 in GOOD_FOR_OPP):
        do(fpath, team1, team2)
    if (team2 in GOOD_FOR_EGO and team1 in GOOD_FOR_OPP):
        do(fpath, team2, team1)
