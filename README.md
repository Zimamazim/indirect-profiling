# Indirect profiling method for performance coverage

This repo contains code for a research project that was part of my JetBrains internship and my master thesis.

The published thesis can be found [here](https://mazim.cz/dp.pdf). The attached data [here](https://mazim.cz/dp.zip). The ZIP file is 8.7 GB and 41GB after unzipping.

## Abstract

Gathering performance data is usually done through benchmarking. This is a tedious, error-prone and time-consuming process. We introduced a novel indirect method of gathering performance data by profiling a workload that uses the target computation. We designed and implemented a pipeline for gathering performance data about Kotlin stdlib functions on JVM and Native.

We discovered and addressed problems with this method -- inlining, profiling setup, comparing profiles from different platforms and the effect of platform-specific code. We verified the results against control benchmarks. Verification is limited because of the differences in both methods and the number of functions verified.

The method produces good enough data to compare performance of stdlib functions between platforms. A good workload is critical for this method to be better than benchmarking.

---

<img src="https://fit.cvut.cz/static/images/fit-cvut-logo-en.svg" alt="FIT CTU logo" height="200">

This software was developed with the support of the **Faculty of Information Technology, Czech Technical University in Prague**.
For more information, visit [fit.cvut.cz](https://fit.cvut.cz).

