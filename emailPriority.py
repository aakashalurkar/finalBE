#!/usr/bin/env python3
import os
import email.utils
import random
import re 
import string
from datetime import datetime
from collections import Counter
import time
from time import mktime as mktime
import math
import statistics
import webbrowser

from flask import Flask,request,jsonify

app = Flask(__name__)

print("!!! OUTSIDE CODE")

def emailStringIntoList(emailString):

    print("!!! EMAILSTRINGINTOLIST\n")
    #print(emailString)
    listOfEmails = emailString.split(r"Author:")
    #This is necessary because the first element is blank 
    listOfEmails = listOfEmails[1:]
    #print(listOfEmails)
    return listOfEmails
#emailStringIntoList(originalEmailString)
def getFrom(listOfEmails):
    
    print("!!! GETFROM\n")
    fromList = []
    for email in listOfEmails:
        tempIndex = email.find(r"Date:")
        if(email[:tempIndex] == " "):
            fromList.append("null")
        else:
            fromList.append(email[:tempIndex])

    #print("FromList: ", fromList)
    return fromList
#getFrom()

def getDate(listOfEmails):
    
    print("!!! GETDATE\n")
    tempDateValue = []
    dateList = []
    #Returns everything after the date as of now, because the word 'Subject' is directly attached to the last character 
    #of the prev. email, so it can't be searched for
    
    for eachMail in listOfEmails:
        tempDateIndex = eachMail.find(r"Date:")
        tempDateIndex = tempDateIndex + 6
        tempSubjectIndex = eachMail.find(r"Subject: ")
        tempDateValue.append(eachMail[tempDateIndex:tempSubjectIndex])
    for anytime in tempDateValue:
        d = time.mktime(email.utils.parsedate(anytime))
        dateList.append(d)
    #print("DateList: ", dateList)   
    return dateList
#getDate()

def getSubject(listOfEmails): 

    print("!!! GETSUBJECT\n")
    subjectList = []
    for email in listOfEmails:
        tempSubjectIndex = email.find(r"Subject:")
        tempSubjectIndex = tempSubjectIndex + 9
        #print(email[tempSubjectIndex:])
        subjectList.append(email[tempSubjectIndex:])
    #print("SubectList: ", subjectList)
    return subjectList
#getSubject()
def getSenderFreq(listOfEmails):

    print("!!! GETSENDERFREQ\n")
    fromList = getFrom(listOfEmails)
    countId = []
    senderFreqDict = {}
    for i in fromList:
        countId.append(math.log(fromList.count(i) + 1))
    senderFreqDict = dict(zip(fromList, countId))
    #print("Sender's dict:", senderFreqDict)
    return senderFreqDict
#getSenderFreq(listOfEmails)

def parseEmail(listOfEmails):
    
    print("!!! PARSEEMAIL\n")
    parsedVectorUnsorted = []
    parsedVector = []
    fromList = getFrom(listOfEmails)
    dateList = getDate(listOfEmails)
    subjectList = getSubject(listOfEmails)
    senderFreqLog = getSenderFreq(listOfEmails)
    for i in range(len(fromList)):
        parsedVectorUnsorted.append([dateList[i], fromList[i], subjectList[i]]) #, bodyList[i], senderFreqLog[i]])
        
    #sorting by value of date ie newest to oldest or vice versa
    parsedVector = sorted(parsedVectorUnsorted, key = lambda x: x[0])  
    
    return parsedVector
#parseEmail()
def emailThread(listOfEmails):
    
    print("!!! EMAILTHREAD\n")
    parsedVector = parseEmail(listOfEmails)
    threadVector = []
    tempSubjectCount = []
    tempThreadSubjectsList = []
    tempThreadSubjectsSet = []
    tempSubjectCountInSet = []
    maxTimeInThread = []
    minTimeInThread = []
    timeDiffInThread = []
    ratio = []
    affineWeight = []
    finalTable = []
    threadWeightsMatrix = []
    threadNameWeightDict = {}
    
    for i in range(len(parsedVector)):
        if(r"Re:" in parsedVector[i][2]):
            threadVector.append([parsedVector[i][0], parsedVector[i][1], parsedVector[i][2][4:]])
            
    for row in threadVector: 
        tempThreadSubjectsList.append(row[2]) 

    for line in tempThreadSubjectsList:
        count = 0
        for i in range(len(tempThreadSubjectsList)):
            if(tempThreadSubjectsList[i] == line):
                count = count + 1
        tempSubjectCount.append(count)

# Alternate method for above block:
#     for line in tempThreadSubjectsList:
#         tempSubjectCount.append(tempThreadSubjectsList.count(line))
    
    tempThreadSubjectsSet = list(set(tempThreadSubjectsList))

    for i in range(len(tempThreadSubjectsSet)):
        for j in range(len(tempThreadSubjectsList)):
            if(tempThreadSubjectsSet[i] == tempThreadSubjectsList[j]):
                tempSubjectCountInSet.append(tempSubjectCount[j])
                break
        
    for sub in range(len(tempThreadSubjectsSet)):
        temp = []
        for row in range(len(threadVector)):
            if(tempThreadSubjectsSet[sub] in threadVector[row][2]):
                temp.append(threadVector[row][0])
            #Right now, we have no thread emails; remove else ASAP 
            else:
                temp.append(0)
        maxTimeInThread.append(max(temp))
        minTimeInThread.append(min(temp))
    
    for i in range(len(maxTimeInThread)):
        timeDiffInThread.append(maxTimeInThread[i] - minTimeInThread[i])
    for y in range(len(timeDiffInThread)):
        if(timeDiffInThread[y] == 0.0):
            timeDiffInThread[y] = 1000000000
 
    for i in range(len(timeDiffInThread)):
        #print(howManyMessages[i], timeDiff[i])
        ratio.append(tempSubjectCountInSet[i]/timeDiffInThread[i])

    for x in range(len(ratio)):
        affineWeight.append(math.log(ratio[x], 10) + 10)

    for i in range(len(tempThreadSubjectsSet)):
        threadWeightsMatrix.append([tempThreadSubjectsSet[i], tempSubjectCountInSet[i], 
                                    timeDiffInThread[i], affineWeight[i]])
        
    threadNameWeightDict = dict(zip(tempThreadSubjectsSet, affineWeight))


#     return (tempThreadSubjectsSet, affineWeight)
    return (threadNameWeightDict, tempThreadSubjectsSet)


def getFromForSingleEmail(email):
    
    print("!!! FROMFORSINGLEEMAIL\n")
    tempIndex = email.find(r"Date:")
    fromValue = ""
    if(email[:tempIndex] == " "):
        fromValue = "null"
    else:
        fromValue = email[:tempIndex]
    return fromValue

def getSubjectForSingleEmail(email):
    
    print("!!! SUBJECTFORSINGLEEMAIL\n")
    subjectLine = ""
    tempIndex = email.find(r"Subject:")
    tempIndex = tempIndex + 9
    subjectLine = email[tempIndex:]
    return subjectLine

@app.route("/", methods=['GET'])
def generateRank():
    
    print("!!! GENERATERANK\n")
    originalEmailString = request.args.get("emailList")
    print("!!! ORIGINALEMAILSTRING\n", originalEmailString)
    #change IP address
    #urlString = "http://192.168.2.5:5000/?emailList=" + originalEmailString
    #urlString = "http://google.com"
    #webbrowser.open_new_tab(urlString)
    
    listOfEmails = emailStringIntoList(originalEmailString)
    termCountsInBodies = []
    fromSenderFrequencyDict = {}
    threadAffineWeight = []
    threadAffineWeightDict = {}
    termCountsInSubjects = []
    emailVector = parseEmail(listOfEmails)
    fromCount = []
    threadWeightCount = []
    rankersList = []
    emailsAndRanksDict = {}
    emailsAndRanksAndPrioList = []
    tempPrioList = []
    finalFinalTable = []
    listOfAllSubjects = []  
    tempRankersList = []
    medianValueOfRanks = 0
    prioList = []
    notPrioList = []
    finalFinalTableString = ""
    
    #feature 2 - frequency of sender
    fromSenderFrequencyDict = getSenderFreq(listOfEmails)
    print("FROMSENDERFREQDICT", fromSenderFrequencyDict)
    #feature 3 - thread weight ie threadAffineWeight
    threadAffineWeightDict, tempSubjectSet = emailThread(listOfEmails)   
    
    for eachEmail in listOfEmails:
        tempNumber = 0 
        fromValue = getFromForSingleEmail(eachEmail)
        subjectLine = getSubjectForSingleEmail(eachEmail)
        listOfAllSubjects.append(getSubjectForSingleEmail(eachEmail))
        
        for key in fromSenderFrequencyDict:
            if(fromValue == key):
                tempNumber = fromSenderFrequencyDict[key]
                
        for key in threadAffineWeightDict: 
            if(subjectLine == key):
                tempNumber = (tempNumber*threadAffineWeightDict[key])
            else:
                tempNumber = (tempNumber*1)
                
        rankersList.append(tempNumber)
        
    emailsAndRanksDict = dict(zip(listOfEmails, rankersList))
    print("!!! DICTIONARY OF EMAILS AND RANKS\n", emailsAndRanksDict, "\n!!! LENGTH OF EMAILSANDRANKSDICT", len(emailsAndRanksDict))
    medianValueOfRanks = statistics.median(rankersList)
    print("!!! MEDIANVALUEOFRANKS: ", medianValueOfRanks)
    for email in emailsAndRanksDict:
        if(emailsAndRanksDict[email] > medianValueOfRanks):
            emailsAndRanksAndPrioList.append("PRIORITY")
            prioList.append(1)
        else:
            emailsAndRanksAndPrioList.append("NOT_PRIORITY")
            notPrioList.append(1)
        
    #fromIds = []
    #for temp in fromSenderFrequencyDict:
    #    fromIds.append(temp) 
    print("LENGTH OF FROMSENDERFREQDICT", len(fromSenderFrequencyDict))  
    print("LENGTH OF LISTOFALLSUBECTS", len(listOfAllSubjects))
    print("LENGTH OF EMAILANDRANKSANDPRIOLIST", len(emailsAndRanksAndPrioList))
    print("LENGTH OF PRIO LIST", len(prioList))
    print("LENGTH OF NON PRIO LIST", len(notPrioList))

    for dude in range(len(listOfAllSubjects)):
        if(emailsAndRanksAndPrioList[dude] == "PRIORITY"):
            #finalFinalTable.append([fromIds[dude], listOfAllSubjects[dude], emailsAndRanksAndPrioList[dude]])
            finalFinalTable.append([listOfAllSubjects[dude], emailsAndRanksAndPrioList[dude]])

    
    print("!!! JUSTRETURNINGFINALVALUE\n")
    #print("!!! FINALTABLE\n",finalFinalTable)
    #print("\nTotal Emails: ", len(rankersList),  "\nPriority Emails: ", len(prioList), "\nNon Priority Emails: ", len(notPrioList))
   # return finalFinalTable
    finalFinalTableString = ''.join(str(e) for e in finalFinalTable)
    print("!!! FINALFINALTABLESTRING\n",finalFinalTableString)
    return finalFinalTableString

if __name__ == '__main__':
    app.debug = True
    print("!!! MAINFUNC\n")
    app.run(host="0.0.0.0")


