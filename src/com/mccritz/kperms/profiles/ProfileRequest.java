package com.mccritz.kperms.profiles;

public interface ProfileRequest<R> {

    void onComplete(R result);
}
