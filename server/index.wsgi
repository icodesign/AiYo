import sae
from hackday import wsgi

application = sae.create_wsgi_app(wsgi.application)
