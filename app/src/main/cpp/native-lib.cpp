#include <jni.h>
#include <string>
#include <sstream>


extern "C" JNIEXPORT jstring JNICALL
Java_com_kuttatech_gpstest_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "This is a string from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_kuttatech_gpstest_LocationService_locationChanged(
        JNIEnv* env,
        jobject us, /* this */
        jobject locObj)
{
    // Find the Location.getLatitude() and Location.getLongitude() methods.
    //
    // ref: https://developer.android.com/reference/android/location/Location
    //      (all of the methods listed therein are fair game here)
    // ref: https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html#wp16432
    //      (for signatures)
    jclass locClass = env->GetObjectClass(locObj);
    jmethodID getLat = env->GetMethodID(locClass, "getLatitude", "()D");
    jmethodID getLon = env->GetMethodID(locClass, "getLongitude", "()D");

    // Invoke them, i.e. read out the lat/lon.
    jdouble lat = env->CallDoubleMethod(locObj, getLat);
    jdouble lon = env->CallDoubleMethod(locObj, getLon);

    // For an example and troubleshooting, read the lat/lon back to the caller.
    std::stringstream s;
    s << "lat: " << lat << " lon: " << lon;
    return env->NewStringUTF(s.str().c_str());
}
