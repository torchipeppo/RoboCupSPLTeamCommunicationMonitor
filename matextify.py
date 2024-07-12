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
GOOD_FOR_OPP_ONLY = {"HULKs", "SPQR-Team", "NomadZ", "Bembelbots"}
GOOD_FOR_ANY = GOOD_FOR_OPP_ONLY.union(GOOD_FOR_EGO)

BASE_DIRECTORY = Path(__file__).resolve().parent
LOGDIR = BASE_DIRECTORY.parent / "RoboCup2023Logs"
BINDIR = BASE_DIRECTORY / "bin"

def call_tcm_to_generate_csv(fpath):
    cmdlist = ["java", "-jar", "TeamCommunicationMonitor.jar", "-t",  str(fpath)]
    print("Running", " ".join(cmdlist))
    with chdir(BINDIR):
        subprocess.run(cmdlist)

def handle_csv(csvpath):
    # setup
    with open(csvpath) as f:
        input_lines = f.readlines()
    header_str = input_lines[0]
    team1_lines = [header_str]
    team2_lines = [header_str]
    header_list = header_str.split(",")
    team_idx = header_list.index("team")

    # split upp csv lines
    for line_str in input_lines[1:]:
        line_list = line_str.split(",")
        try:
            the_team = line_list[team_idx]
        except IndexError:
            continue  # handle possible final empty line
        if int(the_team) == TEAM_IDs[team1]:
            team1_lines.append(line_str)
        elif int(the_team) == TEAM_IDs[team2]:
            team2_lines.append(line_str)
        else:
            raise Exception("Non Eurosedia")

    # write for the high-quality ego-teams
    if team1 in GOOD_FOR_EGO:
        p = csvpath.parent / (csvpath.stem + "__" + team1 + ".csv")
        with open(p, "w") as f:
            f.writelines(team1_lines + ["\n"])
        print("Written", p)
    if team2 in GOOD_FOR_EGO:
        p = csvpath.parent / (csvpath.stem + "__" + team2 + ".csv")
        with open(p, "w") as f:
            f.writelines(team2_lines + ["\n"])
        print("Written", p)

for fpath in LOGDIR.rglob("*.yaml"):
    _log, _date, _time, team1, team2 = fpath.stem.split("_")

    if (
        (team1 in GOOD_FOR_EGO and team2 in GOOD_FOR_ANY) or
        (team2 in GOOD_FOR_EGO and team1 in GOOD_FOR_ANY)
    ):
        call_tcm_to_generate_csv(fpath)

        # not sure how this works, so let's concretize this list right away
        # since I'm going to create and delete a lot of CSVs
        csv_paths_list = list(fpath.parent.glob(fpath.name + "*.csv"))
        for csvpath in csv_paths_list:
            print("Splitting", csvpath.name)
            handle_csv(csvpath)
            csvpath.unlink()
