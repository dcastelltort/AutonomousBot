#include <jni.h>
#include <string>


extern "C"
jstring
Java_com_dcastelltort_robothobby_autonomousbot_MainActivity_coreProcess(
        JNIEnv *env,
        jobject /* this */) {
    //this is our loop

    //read last measures

    //process and take decision

    //push commands to be executed

    // place holder code to track coreProcess calls
    std::string msg;
    static int nbCalls = 0;
    nbCalls++;
    if (nbCalls >= 30) {
        nbCalls = 0;
        msg = "from coreProcess";

    }
    return env->NewStringUTF(msg.c_str());
}