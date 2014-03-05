.. _-proxyTo-:

proxyTo
==========

Proxy the request to the given Uri.

Signature
---------

.. includecode:: /../spray-routing/src/main/scala/spray/routing/directives/ProxyDirectives.scala
   :snippet: proxyTo

Description
-----------

The ``proxyTo`` directive can be used to proxy an ``HttpRequest`` to an external http service

Example
-------

.. includecode:: ../code/docs/directives/ProxyDirectivesExamplesSpec.scala
   :snippet: example-1