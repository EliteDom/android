How to Contribute Code to Elitedom
==============================

Thank you for your interest in contributing to Elitedom!  We strongly welcome and appreciate such contributions!

This short guide aims to provide hints and pointers to making the process as quick and painless as possible.

Elitedom Source Code
----------------

The source code for Elitedom [Android] is kept in [GitHub](http://github.com/EliteDom/android) and follows mainly the pull-request model;
the primary branch for development is `master`.

Contributing Your Changes
----------------

Before implementing your changes, create a new branch that will act in insolation to master. This is your development branch. It is suggested strongly to name the branch on the basis of your contribution.
`git checkout -b <branch-name>`
After implementing your changes, add and commit your changes.
`git add dir/to/file.extension`
`git commit -m "<Description of Contribution>"`
`git push --set-upstream origin <branch-name>`
Your changes are now in your forked repository on GitHub. Now, create a pull request back to the main development repository.

Once a pull request is created, a member of the Elitedom team will review it as quickly as possible. It may be beneficial to add a suggested reviewer to the PR in order to get quicker attention. The reviewer will initiate a build of your pull request, review your pull request, provide feedback and in the end hopefully merge it.
