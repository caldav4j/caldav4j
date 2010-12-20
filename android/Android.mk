LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := libccodec libclang libchttpc libwebdav libbkport libehcache

LOCAL_SRC_FILES := $(call all-java-files-under,src)

# TODO: Remove dependency of application on the test runner (android.test.runner)
# library.
LOCAL_JAVA_LIBRARIES := android.test.runner

LOCAL_STATIC_JAVA_LIBRARIES += android-common

LOCAL_PACKAGE_NAME := Calendar

#LOCAL_JNI_SHARED_LIBRARIES := libcloudfs

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libccodec:lib/commons-codec-1.3.jar \
					libclang:lib/commons-lang-2.4.jar \
					libchttpc:lib/commons-httpclient-3.0.jar \
					libwebdav:lib/jakarta-slide-webdavlib-2.2pre1-httpclient-3.0.jar \
					libbkport:lib/backport-util-concurrent.jar \
					libehcache:lib/ehcache-1.2.jar \

include $(BUILD_MULTI_PREBUILT)



# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
