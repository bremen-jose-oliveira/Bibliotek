const path = require('path');

module.exports = {
  mode: 'development',
  entry: './src/main/resources/static/ts/index.ts', // Entry point
  output: {
    filename: 'bundle.js', // Output bundle
    path: path.resolve(__dirname, 'src/main/resources/static/dist'), // Output directory
    publicPath: '/static/dist/', // Ensure webpack knows where to serve the files from
  },
  resolve: {
    extensions: ['.ts', '.js'], // Resolve these extensions
  },
  module: {
    rules: [
      {
        test: /\.ts$/, // Apply this rule to .ts files
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.css$/, // Apply this rule to .css files
        use: ['style-loader', 'css-loader'],
      },
    ],
  },
  devtool: 'source-map', // Enable source maps
  devServer: {
    static: {
      directory: path.join(__dirname, 'src/main/resources/static'), // Serve from this directory
    },
    compress: true,
    port: 9000,
  },
};
