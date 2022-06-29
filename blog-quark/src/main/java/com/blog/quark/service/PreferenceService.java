package com.blog.quark.service;

import java.util.List;

import com.blog.quark.entity.Preference;

public interface PreferenceService {
    List<Preference> getAll();
    long add(List<Preference> list) throws RuntimeException;
    long delete(List<Preference> list) throws RuntimeException;
}
