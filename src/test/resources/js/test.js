/*jslint node: true*/
/*jshint node: true*/

var express = require('express');
var webpack = require('webpack');
var config = require('./webpack.config.dev');
var webpackDevMiddleware = require('webpack-dev-middleware');

const app = express();
const compiler = webpack(config);

app.use(function(req, resp, next) {
  console.log('requested resource: ' + req.url);
  next();
});

app.use(express.static(__dirname + '/src/assets'));

const devMiddleware = webpackDevMiddleware(compiler, {
  noInfo: false,
  publicPath: config.output.publicPath
});

app.use(devMiddleware);
app.use(require('webpack-hot-middleware')(compiler));

app.get('*', function(req, res) {
  //https://github.com/jantimon/html-webpack-plugin/issues/3#issuecomment-271804797
  //Here is it! Get the index.html from the fileSystem
  const htmlBuffer = devMiddleware.fileSystem.readFileSync(`${config.output.path}/index.html`)
  res.send(htmlBuffer.toString())
});

app.listen(7770, function(err) {
  if (err) {
    console.log(err);
    return;
  }

  console.log('🌎 Listening at http://localhost:7770');
});
