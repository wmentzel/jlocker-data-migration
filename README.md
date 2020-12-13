# jLocker Data Migration

A small program to convert jLocker's old data format to a more modern approach. Java serialization may break between
major releases. In fact it did from Java 7 to Java 8. The new format is JSON which encrypts sensitive parts of the data.