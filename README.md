# jLocker
a Java locker manager

#Introduction

I wrote this Java Swing application for my former school at which I made my Abitur. I started working on it in 2008 and the first version was released 2009. This application is as far as I know used to this day.

I had to change the name from “jLock” to “jLocker” because there already was a product with that name.

#Features

__User Management:__ by default two users can log-in, one with superuser privileges and one with limited privileges (cannot see lock codes)

__Optimization:__ if a (school) class is moved to another room, jLocker can automatically move the pupil’s lockers as well by finding lockers with the shortest distance to the new class room also considering pupils heights (some lockers are to far up for 5th graders to reach)

__Security:__ all data is DES encrypted with a password of a length of at least 8 digits

__Ease of Use:__ jLocker uses a hierarchy of buildings, floors, walks and managment units (which are either a room or a staircase) which makes it possible to represent any layout a school or university may have. All this can be created easily at run time.

__Data Is Yours:__ jLocker works completely offline and only saves data locally on your PC

__Platform Independent:__ jLocker can run on any operating system (any version of Windows, Mac OS or Linux) for which there is a Java Virtual Machine

__Scalability__: the application can be used in any resolution from 800×600 up

__Search:__ you can search for any criteria of a locker within jLocker (pupils name, height…)

__Free:__ jLocker is open source and can be used by anyone for free

__Backups:__ You can specify the number of data states that should be preserved to recover data that was deleted unintentionally.
