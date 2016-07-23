package com.mccritz.kperms.profiles;

import java.util.UUID;

public class BasicProfileLoader implements ProfileLoader {

    private ProfileRequest<Profile> callback;

    public BasicProfileLoader(UUID id, ProfileRequest<Profile> callback) {
        this.callback = callback;

        new Profile(id).loadProfileData(callback);
    }

    public BasicProfileLoader(Profile provided, ProfileRequest<Profile> callback) {
        this.callback = callback;

        callback.onComplete(provided);
    }

    @Override
    public ProfileLoader onComplete(ProfileRequest<Profile> callback) {
        this.callback = callback;
        return this;
    }
}
