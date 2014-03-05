.. _-proxyToUnmatchedPath-:

proxyToUnmatchedPath
==========

Proxy the request to the given Uri and completing with the unmatchedPath.

Signature
---------

.. includecode:: /../spray-routing/src/main/scala/spray/routing/directives/ProxyDirectives.scala
   :snippet: proxyToUnmatchedPath

Description
-----------

The ``proxyToUnmatchedPath`` directive can be used to proxy an ``HttpRequest`` to an external http service with the
unmatchePath, it can be convenient for proxy a old api for example

Example
-------

.. includecode:: ../code/docs/directives/ProxyDirectivesExamplesSpec.scala
   :snippet: example-2