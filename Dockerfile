FROM ubuntu:latest
LABEL authors="dr2p"

ENTRYPOINT ["top", "-b"]