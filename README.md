# Mindsync

## Introduction
An mHealth application for answering mental health self-assessment questionnaires with wearable data collection. 
This application makes use of the open source adaptive IDPT framework found [here](https://github.com/sureshHARDIYA/idpt#components-of-idpt).
It fetches questionnaires from the IDPT framework where therapists can make them and sends a questionnaire response back. 
This is a novel solution that incorporates wearable data into the answer process. It makes use of the [Health Connect API](
https://developer.android.com/health-and-fitness/guides/health-connect) to read raw and aggregated data that can be used instead of self-reporting by the patient when answering the questionnaire. 

Commercialized wearable technology is an emerging technology that shows great potential. In the healthcare sector, where workers often have limited time and resources are scarce
wearable technology can be utilized to fill these gaps. Traditional questionnaires in the domain of mental health suffers from response burden, social desirability bias
and other challenges and limitations posed on the patient and the therapist. Be utilizing wearable data collection there is a chance that the answer provided
is more objective and can be reliably used to detect and help patients early in their treatment plan. 

The use of this app and other apps can enhance patient health and help therapists in many way: 

1. Treatment planning
2. Accurate assessment
3. Less time spent
4. Objective monitrong
5. Early diagnosis and help
6. Can potentially remove the stigma and challenges regardging these traditional assessment methods

## IDPT 
The IDPT framework referenced in the introduction facilitates views for creating questionnaires and view the responses to them. Below is a demo showcasing the extension of the IDPT:


https://github.com/P1T1B0Y98/MindSync/assets/90247464/4b3b2d2b-bfb4-44ad-8acd-3f291ddef5b0


## Demo MindSync

This holds the demo for the MindSync application that facilitates. The first vidoe shows the register and login process in addition to the setup phase and the settings functionalities (NB! Turn on sound)

https://github.com/P1T1B0Y98/MindSync/assets/90247464/2c690a1d-1710-47f8-8a86-d5a16633a9c8

Below shows the answer process and the visualization of results, in addition to stress values we visualize sleep and physical activity as seen in the documentation. 


https://github.com/P1T1B0Y98/MindSync/assets/90247464/44473767-9f3c-49a0-9f13-9fb9db89498c


## Getting started
For future development and an explanation of the important functions and screens take a look at the [Gitbook repo for MindSync](https://mindsync.gitbook.io/mindsync/).

## Features

- Wearable data integration

- Digital platform for answering questionnaires

- Delete responses

- Health Connect integration where one can read and write data from other apps. This makes it possible for other apps to reuse our data if we facilitate it.

## Tags



