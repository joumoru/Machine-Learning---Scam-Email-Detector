# Machine-Learning---Scam-Email-Detector
This repo hosts a machine learning model for scam email detection. It uses the Naive Bayes algorithm and Weka library to classify emails as spam or not-spam. Features include word frequency, character frequency, and capital letter runs. Robust and accurate, this model protects users from phishing attempts.
Spam Email Classifier
This project is a Java implementation of a spam email classifier using Naive Bayes classifier from the Weka machine learning library. This classifier model is trained on a dataset of email contents, learning how to distinguish spam from non-spam emails.

Project structure
The main class, ClientHandler, handles client connections, each of which is expected to send an email content for classification.

Main methods
public ClientHandler(Socket clientSocket)
This is the constructor of the ClientHandler class. It initializes the socket used for communication with the client and the Naive Bayes model used for email classification.

private String classifyEmail(String emailContent)
This method classifies an email as either spam or non-spam. It first pre-processes the email content to generate appropriate features and then uses the Naive Bayes model to classify the email.

private Instance extractFeatures(String emailContent)
This method extracts features from the email content. These features are then used to create an instance which is then classified by the Naive Bayes model.

private void initNaiveBayesModel() throws IOException
This method initializes the Naive Bayes model. It loads a dataset from an ARFF file, splits the dataset into a training set and a testing set, and then builds the Naive Bayes model using the training set.

private double calculateAccuracy(Instances testInstances)
This method calculates the accuracy of the Naive Bayes model by classifying each instance in the testing set and comparing the classification result with the actual class value.

public void run()
This method is the entry point for the thread. It reads an email content from the client, classifies the email, and sends the classification result back to the client.

How to run
To run this code, you need to have Weka library and Java installed in your environment. Also, make sure you have the ARFF file containing the dataset. You can customize the path of the dataset file in initNaiveBayesModel() method.

Compile the Java code:
Copy code
javac ClientHandler.java
Run the compiled Java program:
Copy code
java ClientHandler
Please note that you need to write additional code to accept connections from clients and start a new ClientHandler for each connection in a new thread.

Dependencies
Weka Library: This library provides the implementation of the Naive Bayes classifier used in this project. You can download it from https://waikato.github.io/weka-wiki/downloading_weka/
Java Development Kit (JDK) - You need to have the JDK installed to compile and run the Java code. You can download it from https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
Note
This project is for educational purposes and should not be used as-is for real-life spam detection, as it simplifies many aspects of the problem.
