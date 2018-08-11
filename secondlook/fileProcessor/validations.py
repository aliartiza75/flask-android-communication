###################################################################################################
# Name: validations.py
# Summary: File contains methods for validations
# Author(s): Irtiza Ali
# LastUpdated: 10-03-2018
###################################################################################################
import os
import sys
import copy
from werkzeug.utils import secure_filename


sys.path.insert(0, os.path.join(os.path.dirname(os.path.realpath(__file__))))
# dev defined Modules
import configs as cfg
configs = copy.deepcopy(cfg.configs)

def allowed_file(filename):
    '''
    Checks if file has a valid format
    '''
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in configs["ALLOWED_EXTENSIONS"]

def save_file(file, file_path):
    """
    It will save a file in the fileDirectory folder

    input args:
    file : [file] File object
    """
    filename = secure_filename(file.filename)
    try:
        file.save(os.path.join(file_path, filename))
        return True
    except:
        return False

def remove_file(file_path):
    """
    It will remove a file specified by the user

    input args:
    file_path: [str] path to the file that needs to be removed
    """
    try:
        os.remove(file_path)
        return True
    except:
        return False

def calculate_result(file_path):
    '''
    It will read the file and count the numbers of "a" and "na" tags
    '''
    with open(file_path, 'r') as result_file:
        result_data = result_file.read()
        a_count = 0
        na_count = 0
        for line in result_data:
            if (line[-2] == 'a') and (line[-3] == " "):
                a_count = a_count + 1
            elif (line[-3] == 'n') and (line[-4] == " "):
                na_count = na_count + 1
        
        return a_count, na_count, result_data