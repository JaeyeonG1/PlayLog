#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_quickids_playlog_activity_CvRecordActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                                     jlong mat_addr_input,
                                                                     jlong mat_addr_result) {
    // TODO: implement ConvertRGBtoGray()
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;

    // GrayScale
    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
    // Blur 효과 (중앙값 블러 적용) [blur/GaussianBlur/bilateralFilter]
    medianBlur(matResult, matResult, 2);
    // 이진화 (in, out, 임계값, 최댓값, 임계값 종류[0-255])
    threshold(matResult, matResult, 100, 255, 0);
}