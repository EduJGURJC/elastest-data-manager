# coding: utf-8

import sys
from setuptools import setup, find_packages

NAME = "edm"
VERSION = "0.5.0"

# To install the library, run the following
#
# python setup.py install
#
# prerequisite: setuptools
# http://pypi.python.org/pypi/setuptools

REQUIRES = ["connexion"]

setup(
    name=NAME,
    version=VERSION,
    description="ElasTest Data Manager API",
    author_email="elastest-users@googlegroups.com",
    url="https://github.com/elastest/bugtracker",
    keywords=["Swagger", "ElasTest Service Manager API"],
    install_requires=REQUIRES,
    packages=find_packages(),
    package_data={'': ['swagger/swagger.yaml']},
    include_package_data=True,
    long_description="""\
    This is the data manager API.
    """
)