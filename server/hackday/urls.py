#! /usr/bin/env python
#coding=utf-8

from django.conf.urls import patterns, include, url
from hackday.service.views  import *
# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'hackday.views.home', name='home'),
    # url(r'^hackday/', include('hackday.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
    url(r'^test/$', test_service),
    url(r'^insert_photo/$', insert_photo),
    url(r'^get_photo/$', get_photo),
    url(r'^timeline/$', get_timeline),
    url(r'^update/photo/$', update_photo_description),
    url(r'^update/gallery/$', update_gallery_description),
)
