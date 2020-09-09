# imports
import sys
import os
import requests
import json

def POS (SERIAL):
    _auth = ('admin', '1725ipatadmin')
    _headers = {'content-type': 'application/json'}

    # computational task
    URL = "https://ipatproject.pythonanywhere.com/api/computation/%d/" % (SERIAL)
    body = json.dumps({"is_inProcess":False})
    r = requests.patch(URL,
                    data = body,
                    headers = _headers,
                    auth = _auth)

    # txt/csv
    filesTXT = [file for file in os.listdir() if (".txt" in file) or (".csv" in file)]
    for file in filesTXT:
        with open(file, "rb") as fileRead:
            print(file)
            r = requests.post("https://ipatproject.pythonanywhere.com/api/resultFile/",
                            files = {'resultFile': fileRead},
                            data = {"computation_id":SERIAL, "title":file},
                            auth = _auth)
            print(r)

    # images
    filesPNG = [file for file in os.listdir() if ".png" in file]
    for file in filesPNG:
        with open(file, "rb") as fileRead:
            print(file)
            r = requests.post("https://ipatproject.pythonanywhere.com/api/resultImage/",
                            files = {'imageFile': fileRead},
                            data = {"computation_id":SERIAL, "title":file},
                            auth = _auth)
            print(r)


if __name__ == "__main__":
    POS(int(sys.argv[1]))


# args = [
#     "-SERIAL", "201929493",
#     "-wd", "/user/chingan",
#     "-project", "title"
# ]




# curl \
#   -X POST \
#   -u admin:1725ipatadmin \
#   -F "computation_id=1" \
#   -F "imageFile=@/data/james/api1.png" \
#   https://ipatproject.pythonanywhere.com/api/resultImage/
