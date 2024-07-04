# Worklog

Worklog is a simple multiplatform CLI application to log what you are currently working on. It is written with Kotlin Native, and utilizes the awesome [clikt](https://github.com/ajalt/clikt) library.

## Features

It currently supports:
* Adding log items
* Listing log items
  * Listing with, or without timestamps
  * Depending on the terminal, printing either uses colors, or is simplified
* Use a custom configuration file

## Known issues

* The application currently can not support UTF-8 charset in Windows, so characters like áéíóöőúüű will not be displayed correctly. This is a limitation of the Kotlin Native platform and not the application itself.

## Usage

Adding log items:
![add](https://github.com/tothlp/worklog/assets/16169630/d6733e99-04f7-4023-9a61-5a64aa2adab9)

Listing log items:
![list](https://github.com/tothlp/worklog/assets/16169630/104c2ad3-ba76-4a19-b7e8-211a764989a0)

For more information, use the `--help` flag.

## Installation

Simply download from the Releases section, and run without any installation. The extension of the macOS binary can be misleading, but do not be alarmed by the `.kexe` extension. You can run it as it is, or rename the executable as you wish.

## Configuration

The application saves the log items by default to `~/.worklog`. You can change this by creating a configuration file `~/.worklogrc`. 
The configuration file can contain the following:

| Key | Description | Default    |
| --- | --- |------------|
|showTimestamps|Whether to show timestamps in the list command| false      |
|dataFile | The file to save the log items to | ~/.worklog |

Example configuration file:
```json
{
  "showTimestamps": false,
  "dataFile": "/path/to/your/file"
}
```