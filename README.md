# Catalog example.

5) Mock support.
HttpHelper provides an abstraction for http access, this abstraction allow us to define mock
responses.
The mock support is implemented using a product flavor defined in build.gradle.
The mock of service call are located in scr/mock/resource/ and are only included in "mock" builds.

6) Data.
If catalog could be very large, we need to know a bit more about how the app should work.
- If catalog change very often: The order and filter should be made by remote service and the
  application should implement pagination.
- If the app should support offline search in catalog: we can load all catalog data from service
  and implement order and filter in the app.
For this example we will use the second approach in order to implement the filter functionality
without service implementation.

7) Concurrency.
ContentProvider access is not thread safe, anyway due the ContentProvider actually wrap a
SQLiteDatabase, and SQLiteDatabase is synchronized, we just delegate synchronization on it.
This synchronization is made in getWritableDatabase() method.